/**
 *    Copyright 2009-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.github.esbatis.executor;

import com.github.esbatis.core.Configuration;
import com.github.esbatis.client.RestClient;
import com.github.esbatis.handler.ResultHandler;
import com.github.esbatis.proxy.MapperMethod;
import com.github.esbatis.core.*;
import com.github.esbatis.handler.DefaultResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 *
 * The default implementation for {@link Executor}.
 * Note that this class is not Thread-Safe.
 *
 * @author Clinton Begin
 */
public class DefaultExecutor implements Executor {

  private static final Logger logger = LoggerFactory.getLogger(DefaultExecutor.class);

  private final Configuration configuration;
  private final RestClient restClient;

  public DefaultExecutor(Configuration configuration) {
    this.configuration = configuration;
    this.restClient = new RestClient(configuration.getHosts());
  }

  @Override
  public Configuration getConfiguration() {
    return configuration;
  }

  @Override
  public <T> T execute(MappedStatement ms, Object[] args) {
    MapperMethod mapperMethod = ms.getMapperMethod();
    Map<String, Object> parameterMap = mapperMethod.convertArgsToParam(args);

    String httpUrl = ms.renderHttpUrl(parameterMap);
    String httpBody = ms.renderHttpBody(parameterMap);
    String resp = restClient.send(httpUrl, ms.getHttpMethod(), httpBody);

    ResultHandler<?> handler = mapperMethod.getResultHandler();
    if (handler == null) {
      handler = new DefaultResultHandler(ms);
    }

    return (T)handler.handleResult(resp);
  }

}
