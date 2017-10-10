package com.github.esbatis.executor;

/**
 * @author jinzhong.zhang
 */
public interface ExecutorFilter {

    void before(FilterContext context);

    void after(FilterContext context);

    void exception(FilterContext context);

}
