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

import com.github.esbatis.core.Configuration;

import java.util.Map;

/**
 * @author Clinton Begin
 */
public class DynamicContext {

  private Map<String, Object> bindings;
  private final StringBuilder sb = new StringBuilder();
  private int uniqueNumber = 0;

  public DynamicContext(Configuration configuration, Map<String, Object> parameters) {
    this.bindings = parameters;
  }

  public Map<String, Object> getBindings() {
    return bindings;
  }

  public void bind(String name, Object value) {
    bindings.put(name, value);
  }

  public int getUniqueNumber() {
    return uniqueNumber++;
  }

  public void appendString(String str) {
    sb.append(str);
    sb.append(" ");
  }

  public String getResult() {
    return sb.toString().trim();
  }

}