/**
 *    Copyright 2009-2016 the original author or authors.
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
package com.github.esbatis.utils;

import com.github.esbatis.parser.ParserException;
import org.mvel2.MVEL;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mvel parsed expressions.
 * @author jinzhong.zhang
 */
public final class MVELUtils {

  private static final Map<String, Serializable> expressionCache = new ConcurrentHashMap<>();

  private MVELUtils() {
    // Prevent Instantiation of Static Class
  }

  public static Object eval(String expression, Object parameterObject) {
    Serializable compiled = expressionCache.get(expression);
    if (compiled == null) {
      compiled = MVEL.compileExpression(expression);
      expressionCache.put(expression, compiled);
    }

    Object result = MVEL.executeExpression(compiled, parameterObject);
    return result;
  }

  public static boolean evalBoolean(String expression, Object parameterObject) {
    Object value = eval(expression, parameterObject);
    if (value instanceof Boolean) {
      return (Boolean) value;
    }
    if (value instanceof Number) {
      return !new BigDecimal(String.valueOf(value)).equals(BigDecimal.ZERO);
    }
    return value != null;
  }

  public static Iterable<?> evalIterable(String expression, Object parameterObject) {
    Object value = eval(expression, parameterObject);
    if (value == null) {
      throw new ParserException("The expression '" + expression + "' evaluated to a null value.");
    }
    if (value instanceof Iterable) {
      return (Iterable<?>) value;
    }
    if (value.getClass().isArray()) {
      int size = Array.getLength(value);
      List<Object> answer = new ArrayList<Object>();
      for (int i = 0; i < size; i++) {
        Object o = Array.get(value, i);
        answer.add(o);
      }
      return answer;
    }
    if (value instanceof Map) {
      return ((Map) value).entrySet();
    }
    throw new ParserException("Error evaluating expression '" + expression + "'.  Return value (" + value + ") was not iterable.");
  }

}
