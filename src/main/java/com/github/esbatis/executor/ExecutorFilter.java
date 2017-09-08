package com.github.esbatis.executor;

import com.github.esbatis.core.MappedStatement;

import java.util.Map;

public interface ExecutorFilter {

  void before(MappedStatement ms, Map<String, Object> parameterMap);

  void after(MappedStatement ms, Map<String, Object> parameterMap, String result);

}
