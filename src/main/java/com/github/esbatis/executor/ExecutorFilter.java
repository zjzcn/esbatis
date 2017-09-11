package com.github.esbatis.executor;

import com.github.esbatis.mapper.MappedStatement;

import java.util.Map;

/**
 * @author jinzhong.zhang
 */
public interface ExecutorFilter {

  void exception(MappedStatement ms, Map<String, Object> parameterMap, Exception e);

  void before(MappedStatement ms, Map<String, Object> parameterMap);

  void after(MappedStatement ms, Map<String, Object> parameterMap, String result);

}
