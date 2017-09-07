package com.github.esbatis.annotations;

import java.lang.annotation.*;

/**
 * @author jinzhong.zhang
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ResultType {
  Class<?> value();
}
