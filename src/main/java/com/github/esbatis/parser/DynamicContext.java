/**
 *    Copyright 2009-2015 the original author or authors.
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
package com.github.esbatis.parser;

import com.github.esbatis.reflection.MetaObject;
import com.github.esbatis.session.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Clinton Begin
 */
public class DynamicContext {

  public static final String PARAMETER_OBJECT_KEY = "_parameter";

  private ContextMap bindings;
  private final StringBuilder sb = new StringBuilder();
  private int uniqueNumber = 0;

  public DynamicContext(Configuration configuration, Object parameterObject) {
    if (parameterObject == null) {
      bindings = new ContextMap(null);
    } else if (parameterObject instanceof Map) {
      bindings = new ContextMap(null);
      bindings.putAll((Map)parameterObject);
    } else {
      MetaObject metaObject = configuration.newMetaObject(parameterObject);
      bindings = new ContextMap(metaObject);
    }
    bindings.put(PARAMETER_OBJECT_KEY, parameterObject);
  }

  public Map<String, Object> getBindings() {
    return bindings;
  }

  public void bind(String name, Object value) {
    bindings.put(name, value);
  }

  public void appendString(String str) {
    sb.append(str);
    sb.append(" ");
  }

  public String getResult() {
    return sb.toString().trim();
  }

  public int getUniqueNumber() {
    return uniqueNumber++;
  }

  private static class ContextMap extends HashMap<String, Object> {
    private static final long serialVersionUID = 2977601501966151582L;

    private MetaObject parameterMetaObject;
    public ContextMap(MetaObject parameterMetaObject) {
      this.parameterMetaObject = parameterMetaObject;
    }

    @Override
    public Object get(Object key) {
      String strKey = (String) key;
      if (super.containsKey(strKey)) {
        return super.get(strKey);
      }

      if (parameterMetaObject != null) {
        return parameterMetaObject.getValue(strKey);
      }

      return null;
    }

    @Override
    public boolean containsKey(Object key) {
      String strKey = (String) key;
      if (super.containsKey(strKey)) {
        return true;
      }

      if (parameterMetaObject != null && parameterMetaObject.getValue(strKey) != null) {
        return true;
      }

      return false;
    }
  }
}