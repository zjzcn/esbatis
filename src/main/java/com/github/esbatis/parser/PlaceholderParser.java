package com.github.esbatis.parser;

import com.github.esbatis.utils.MvelUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author jinzhong.zhang
 */
public class PlaceholderParser {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public String parse(String raw, Map<String, Object> bindings) {
        PlaceholderHandler handler = new PlaceholderHandler(bindings);
        TokenParser parser = new TokenParser("${", "}", handler);
        String result = parser.parse(raw);
        return result;
    }

    private static class PlaceholderHandler implements TokenHandler {

        private Map<String, Object> bindings;

        public PlaceholderHandler(Map<String, Object> bindings) {
            this.bindings = bindings;
        }

        @Override
        public String handleToken(String content) {
            Object value = MvelUtils.eval(content, bindings);
            if (value == null) {
                return "";
            } else if (value instanceof Date) {
                DateFormat df = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                return df.format(value);
            } else {
                return String.valueOf(value);
            }
        }

    }

}
