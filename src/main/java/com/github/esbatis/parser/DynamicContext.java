package com.github.esbatis.parser;

import java.util.Map;

/**
 * @author jinzhong.zhang
 */
public class DynamicContext {

    private Map<String, Object> bindings;
    private final StringBuilder sb = new StringBuilder();
    private int uniqueNumber = 0;

    public DynamicContext(Map<String, Object> parameters) {
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