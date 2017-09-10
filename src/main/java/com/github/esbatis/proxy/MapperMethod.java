package com.github.esbatis.proxy;

import com.github.esbatis.annotations.Param;
import com.github.esbatis.annotations.ResultHandlerType;
import com.github.esbatis.annotations.ResultType;
import com.github.esbatis.mapper.MapperException;
import com.github.esbatis.handler.ResultHandler;
import com.github.esbatis.utils.ClassUtils;
import com.github.esbatis.utils.TypeResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author jinzhong.zhang
 */
public class MapperMethod {

  private final String name;

  private final SortedMap<Integer, String> namedParamMap;

  private final Class<?> returnClass;
  private final ResultHandler resultHandler;
  private final Class<?> resultType;

  public MapperMethod(Method method) {
    final String methodName = method.getName();
    final Class<?> declaringClass = method.getDeclaringClass();
    this.name = buildName(declaringClass, methodName);
    this.returnClass = resolveReturnClass(declaringClass, method);
    this.namedParamMap = resolveParamMap(method);
    this.resultHandler = resolveResultHandler(method);
    this.resultType = resolveResultType(method);
  }

  public Map<String, Object> convertArgsToParam(Object[] args) {
    Map<String, Object> param = new HashMap<>();
    for (Map.Entry<Integer, String> entry : this.namedParamMap.entrySet()) {
      param.put(entry.getValue(), args[entry.getKey()]);
    }
    return param;
  }

  public String getName() {
    return name;
  }

  public Class<?> getReturnClass() {
    return returnClass;
  }

  public Class<?> getResultType() {
    return resultType;
  }

  public ResultHandler<?> getResultHandler() {
    return resultHandler;
  }


  private SortedMap<Integer, String> resolveParamMap(Method method) {
    final Annotation[][] paramAnnotations = method.getParameterAnnotations();
    final SortedMap<Integer, String> map = new TreeMap<>();
    int paramCount = paramAnnotations.length;
    // get names from @Param annotations
    for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
      String name = null;
      for (Annotation annotation : paramAnnotations[paramIndex]) {
        if (annotation instanceof Param) {
          name = ((Param) annotation).value();
          break;
        }
      }
      if (name == null) {
        throw new MapperException("Method[" + this.name + "] parameter[" + paramIndex + "] must has @Param.");
      }
      map.put(paramIndex, name);
    }
    return Collections.unmodifiableSortedMap(map);
  }

  private String buildName(Class<?> mapperInterface, String methodName) {
    String name = mapperInterface.getName() + "." + methodName;
    return name;
  }
  private ResultHandler resolveResultHandler(Method method) {
    Annotation resultAnnotation = method.getDeclaredAnnotation(ResultHandlerType.class);
    ResultHandler handler = null;
    if (resultAnnotation != null) {
      Class<? extends ResultHandler> clazz = ((ResultHandlerType) resultAnnotation).value();
      handler = ClassUtils.instantiateClass(clazz);
    }
    return handler;
  }

  private Class<?> resolveResultType(Method method) {
    Annotation resultAnnotation = method.getDeclaredAnnotation(ResultType.class);
    if (resultAnnotation == null) {
      return null;
    }
    Class<?> clazz = ((ResultType) resultAnnotation).value();
    return clazz;
  }

  private Class<?> resolveReturnClass(Class<?> mapperInterface, Method method) {
    Type resolvedReturnType = TypeResolver.resolveReturnType(method, mapperInterface);
    if (resolvedReturnType instanceof Class<?>) {
      return (Class<?>) resolvedReturnType;
    } else if (resolvedReturnType instanceof ParameterizedType) {
      return (Class<?>) ((ParameterizedType) resolvedReturnType).getRawType();
    } else {
      return method.getReturnType();
    }
  }

}
