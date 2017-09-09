package com.github.esbatis.executor;

import com.github.esbatis.config.Configuration;
import com.github.esbatis.config.MappedStatement;

public interface Executor {

  /**
   * Retrieves current configuration
   * @return Configuration
   */
  Configuration getConfiguration();

  <T> T execute(MappedStatement ms, Object[] args);

}
