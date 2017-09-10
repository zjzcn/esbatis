package com.github.esbatis.parser;

import com.github.esbatis.utils.MVELUtils;

import java.util.Map;

/**
 * @author jinzhong.zhang
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
      Object value = MVELUtils.eval(content, bindings);
      String srtValue = (value == null ? "" : String.valueOf(value));
      return srtValue;
    }

  }

}
