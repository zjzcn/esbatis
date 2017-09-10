package com.github.esbatis.test;

import com.github.esbatis.executor.ExecutorFilter;
import com.github.esbatis.mapper.MappedStatement;

import java.util.Map;

public class TimeSpanFilter implements ExecutorFilter {

    private ThreadLocal<Long> timestamp = new ThreadLocal<>();

    @Override
    public void before(MappedStatement ms, Map<String, Object> parameterMap) {
        timestamp.set(System.currentTimeMillis());
    }

    @Override
    public void after(MappedStatement ms, Map<String, Object> parameterMap, String result) {
        Long start = timestamp.get();
        System.out.println("time=" + (System.currentTimeMillis() - start));
        timestamp.remove();
    }
}
