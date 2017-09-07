package com.github.esbatis.annotations;

import com.github.esbatis.session.ResultHandler;

import java.lang.annotation.*;

/**
 * @author jinzhong.zhang
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ResultHandlerType {
  Class<? extends ResultHandler> value();
}
