package com.github.esbatis.executor;

import com.github.esbatis.client.HttpRequest;
import com.github.esbatis.client.HttpResponse;
import com.github.esbatis.mapper.MappedStatement;

import java.util.Map;

/**
 * @author jinzhong.zhang
 */
public interface ExecutorFilter {

    void before(MappedStatement ms, Map<String, Object> parameterMap, HttpRequest request);

    void after(MappedStatement ms, Map<String, Object> parameterMap, HttpRequest request, HttpResponse response);

    void exception(MappedStatement ms, Map<String, Object> parameterMap, HttpRequest request, String host, Throwable e);

}
