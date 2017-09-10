package com.github.esbatis.executor;

import com.github.esbatis.mapper.MapperFactory;
import com.github.esbatis.mapper.MappedStatement;

public interface Executor {

  /**
   * @return
   */
  MapperFactory getMapperFactory();

  <T> T execute(MappedStatement ms, Object[] args);

}
