package com.github.esbatis.annotations;

import java.lang.annotation.*;

/**
 * @author jinzhong.zhang
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {
    String value();
}
