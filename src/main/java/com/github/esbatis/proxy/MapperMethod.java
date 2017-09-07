/**
 *    Copyright 2009-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.github.esbatis.proxy;

import com.github.esbatis.annotations.Param;
import com.github.esbatis.annotations.ResultHandlerType;
import com.github.esbatis.annotations.ResultType;
import com.github.esbatis.session.*;
import com.github.esbatis.utils.ClassUtils;
import com.github.esbatis.utils.TypeResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Clinton Begin
 * @author Eduardo Macarron
 * @author Lasse Voss
 */
public class MapperMethod {

  private final Method method;
  private final String name;

  private final Class<?> returnClass;


  private final SortedMap<Integer, String> namedParamMap;

  private final ResultHandler resultHandler;
  private final Class<?> resultType;

  public MapperMethod(Method method) {
    final String methodName = method.getName();
    final Class<?> declaringClass = method.getDeclaringClass();
    this.method = method;
    this.name = buildName(declaringClass, methodName);
    this.returnClass = resolveReturnClass(declaringClass, method);
    this.namedParamMap = resolveNamedParams(method);
    this.resultHandler = resolveResultHandler(method);
    this.resultType = resolveResultType(method);
  }

  public Class<?> getReturnClass() {
    return returnClass;
  }

  public Class<?> getResultType() {
    return resultType;
  }

  public boolean hasResultHandler() {
    return resultHandler != null;
  }

  public Object execute(Session session, Object[] args) {
    MappedStatement ms = session.getConfiguration().getMappedStatement(this.name);
    if (ms == null) {
      throw new SessionException("Not find MappedStatement by statement[" + this.name + "].");
    }
    // set method info to mapped statement
    ms.setMapperMethod(this);

    Object result = null;
    CommandType commandType = ms.getCommandType();
    if (commandType == CommandType.INDEX) {
      Object param = convertArgsToParam(args);
      result = session.index(this.name, param);
      return result;
    } else if (commandType == CommandType.UPDATEBYQUERY) {
      Object param = convertArgsToParam(args);
      result = session.updateByQuery(this.name, param);
      return result;
    } else if (commandType == CommandType.DELETE) {
      Object param = convertArgsToParam(args);
      result = session.delete(this.name, param);
      return result;
    } else if (commandType == CommandType.GET) {
      Object param = convertArgsToParam(args);
      result = session.get(this.name, param);
    } else if (commandType == CommandType.SEARCH) {
      Object param = convertArgsToParam(args);
      result = session.search(this.name, param, resultHandler);
    }
    return result;
  }

  private SortedMap<Integer, String> resolveNamedParams(Method method) {
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
      if (name != null) {
        map.put(paramIndex, name);
      }
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

  private Object convertArgsToParam(Object[] args) {
    final int paramCount = this.namedParamMap.size();
    if (args == null) {
      return null;
    } else if (paramCount == 0 && args.length == 1) {
      return args[0];
    } else {
      final Map<String, Object> param = new HashMap<>();
      int i = 0;
      for (Map.Entry<Integer, String> entry : this.namedParamMap.entrySet()) {
        param.put(entry.getValue(), args[entry.getKey()]);
//        // add generic param names (param1, param2, ...)
//        final String genericParamName = GENERIC_NAME_PREFIX + String.valueOf(i + 1);
//        // ensure not to overwrite parameter named with @Param
//        if (!this.namedParamMap.containsValue(genericParamName)) {
//          param.put(genericParamName, args[entry.getKey()]);
//        }
        i++;
      }
      return param;
    }
  }

  private <E> Object convertToDeclaredCollection(Configuration config, List<E> list) {
    Class<?> collectionClass = convertImplementClass(this.returnClass);
    Object collection = ClassUtils.instantiateClass(collectionClass);
//    MetaObject metaObject = config.newMetaObject(collection);
//    metaObject.addAll(list);
    return collection;
  }

  @SuppressWarnings("unchecked")
  private <E> Object convertToArray(List<E> list) {
    Class<?> arrayComponentType = this.returnClass.getComponentType();
    Object array = Array.newInstance(arrayComponentType, list.size());
    if (arrayComponentType.isPrimitive()) {
      for (int i = 0; i < list.size(); i++) {
        Array.set(array, i, list.get(i));
      }
      return array;
    } else {
      return list.toArray((E[])array);
    }
  }

  private static Class<?> convertImplementClass(Class<?> type) {
    Class<?> classToCreate;
    if (type == List.class || type == Collection.class || type == Iterable.class) {
      classToCreate = ArrayList.class;
    } else if (type == Map.class) {
      classToCreate = HashMap.class;
    } else if (type == SortedSet.class) {
      classToCreate = TreeSet.class;
    } else if (type == Set.class) {
      classToCreate = HashSet.class;
    } else {
      classToCreate = type;
    }
    return classToCreate;
  }

}
