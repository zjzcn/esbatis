package com.github.esbatis.handler;


/**
 * @author jinzhong.zhang
 */
public interface ResultHandler<T> {

    T handleResult(String result);

}
