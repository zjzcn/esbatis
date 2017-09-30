package com.github.esbatis.test;

import com.github.esbatis.client.HttpRequest;
import com.github.esbatis.client.HttpResponse;
import com.github.esbatis.executor.ExecutorFilter;
import com.github.esbatis.mapper.MappedStatement;

import java.util.Map;

public class TimeSpanFilter implements ExecutorFilter {

    private ThreadLocal<Long> timestamp = new ThreadLocal<>();

    @Override
    public void exception(MappedStatement ms, Map<String, Object> parameterMap, HttpRequest request, String host, Throwable e) {
        System.out.println("------------exception----------");
        timestamp.remove();
        e.printStackTrace();
    }

    @Override
    public void before(MappedStatement ms, Map<String, Object> parameterMap, HttpRequest request) {
        timestamp.set(System.currentTimeMillis());
    }

    @Override
    public void after(MappedStatement ms, Map<String, Object> parameterMap, HttpRequest request, HttpResponse response) {
        Long start = timestamp.get();
        timestamp.remove();
        System.out.println("time span = " + (System.currentTimeMillis() - start));
    }
}
