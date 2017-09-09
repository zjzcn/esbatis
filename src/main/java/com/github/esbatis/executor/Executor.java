package com.github.esbatis.executor;

import com.github.esbatis.mapper.MapperFactory;
import com.github.esbatis.mapper.MappedStatement;

public interface Executor {

  /**
   * Retrieves current mapperFactory
   * @return Configuration
   */
  MapperFactory getMapperFactory();

  <T> T execute(MappedStatement ms, Object[] args);

}
