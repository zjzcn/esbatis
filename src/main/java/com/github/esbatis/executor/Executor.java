package com.github.esbatis.executor;

import com.github.esbatis.core.Configuration;
import com.github.esbatis.core.MappedStatement;

public interface Executor {

  /**
   * Retrieves current configuration
   * @return Configuration
   */
  Configuration getConfiguration();

  <T> T execute(MappedStatement ms, Object[] args);

}
