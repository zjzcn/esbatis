/**
 * Copyright 2009-2017 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.esbatis.executor;

import com.github.esbatis.client.HttpRequest;
import com.github.esbatis.client.HttpResponse;
import com.github.esbatis.client.RestClient;
import com.github.esbatis.client.RestException;
import com.github.esbatis.handler.DefaultResultHandler;
import com.github.esbatis.handler.ResultHandler;
import com.github.esbatis.mapper.MappedStatement;
import com.github.esbatis.mapper.MapperFactory;
import com.github.esbatis.proxy.MapperMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jinzhong.zhang
 */
public class DefaultExecutor implements Executor {

    private static final Logger logger = LoggerFactory.getLogger(DefaultExecutor.class);

    private final MapperFactory mapperFactory;
    private final RestClient restClient;

    public DefaultExecutor(MapperFactory mapperFactory) {
        this.mapperFactory = mapperFactory;
        this.restClient = mapperFactory.getRestClient();
    }

    @Override
    public <T> T execute(MappedStatement ms, Object[] args) {
        MapperMethod mapperMethod = ms.getMapperMethod();
        Map<String, Object> parameterMap = mapperMethod.convertArgsToParam(args);

        String renderedHttpUrl = ms.renderHttpUrl(parameterMap);
        String renderedReqBody = ms.renderHttpBody(parameterMap);

        HttpRequest httpRequest = new HttpRequest(renderedHttpUrl, ms.getHttpMethod(), renderedReqBody);

        FilterContext context = new FilterContext();
        context.setCommandType(ms.getCommandType());
        context.setHttpMethod(ms.getHttpMethod());
        context.setHttpUrl(ms.getHttpUrl());
        context.setRenderedHttpUrl(renderedHttpUrl);

        executeBefore(context);

        HttpResponse httpResponse;
        try {
            httpResponse = restClient.send(httpRequest);
        } catch (RestException e) {
            context.setException(e);
            context.setHttpHost(e.getHost());
            executeException(context);
            throw e;
        }

        context.setHttpHost(httpResponse.getHost());
        context.setHttpStatusCode(httpResponse.getCode());

        if(httpResponse.getCode() >= 300) {
            RestException e = new RestException(httpResponse.getHost(), "Http response error: " + httpResponse);
            context.setException(e);
            executeException(context);
            throw e;
        }

        executeAfter(context);

        ResultHandler<?> handler = mapperMethod.getResultHandler();
        if (handler == null) {
            handler = new DefaultResultHandler(ms);
        } else {
            logger.debug("ResultHandler[{}] used for statement[{}].", handler.getClass(), ms.getStatement());
        }

        return (T) handler.handleResult(httpResponse.getBody());
    }

    private void executeBefore(FilterContext context) {
        List<ExecutorFilter> executorFilters = mapperFactory.getExecutorFilters();
        for (ExecutorFilter filter : executorFilters) {
            filter.before(context);
        }
    }

    private void executeAfter(FilterContext context) {
        List<ExecutorFilter> executorFilters = mapperFactory.getExecutorFilters();
        for (ExecutorFilter filter : executorFilters) {
            filter.after(context);
        }
    }

    private void executeException(FilterContext context) {
        List<ExecutorFilter> executorFilters = mapperFactory.getExecutorFilters();
        for (ExecutorFilter filter : executorFilters) {
            filter.exception(context);
        }
    }
}
