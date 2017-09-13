package com.github.esbatis.executor;

import com.github.esbatis.mapper.MappedStatement;

/**
 * @author jinzhong.zhang
 */
public interface Executor {

    <T> T execute(MappedStatement ms, Object[] args);

}
