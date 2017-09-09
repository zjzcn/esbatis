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
package com.github.esbatis.parser;

import com.github.esbatis.utils.MvelUtils;

import java.util.Map;

/**
 * @author
 */
public class PlaceholderParser {

  public String parse(String raw, Map<String, Object> bindings) {
    ParameterHandler handler = new ParameterHandler(bindings);
    TokenParser parser1 = new TokenParser("${", "}", handler);
    TokenParser parser2 = new TokenParser("#{", "}", handler);
    String result = parser1.parse(raw);
    result = parser2.parse(result);
    return result;
  }

  private static class ParameterHandler implements TokenHandler {

    private Map<String, Object> bindings;

    public ParameterHandler(Map<String, Object> bindings) {
      this.bindings = bindings;
    }

    @Override
    public String handleToken(String content) {
      Object value = MvelUtils.eval(content, bindings);
      String srtValue = (value == null ? "" : String.valueOf(value));
      return srtValue;
    }

  }

}
