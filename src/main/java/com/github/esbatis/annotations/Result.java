package com.github.esbatis.annotations;

import com.github.esbatis.handler.ResultHandler;

import java.lang.annotation.*;

/**
 * @author jinzhong.zhang
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Result {
    Class<? extends ResultHandler> value();
}
