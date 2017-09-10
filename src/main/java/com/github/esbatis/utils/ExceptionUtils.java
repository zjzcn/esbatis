package com.github.esbatis.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * @author jinzhong.zhang
 */
public class ExceptionUtils {

  private ExceptionUtils() {
    // Prevent Instantiation
  }

  public static Throwable unwrapThrowable(Throwable wrapped) {
    Throwable unwrapped = wrapped;
    while (true) {
      if (unwrapped instanceof InvocationTargetException) {
        unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
      } else if (unwrapped instanceof UndeclaredThrowableException) {
        unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
      } else {
        return unwrapped;
      }
    }
  }

  public static <T> T wrapException(Throwable ex, Class<T> clazz) {
    return ClassUtils.instantiateClass(clazz);
  }
}
