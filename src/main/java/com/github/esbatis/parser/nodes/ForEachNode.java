package com.github.esbatis.parser.nodes;

import com.github.esbatis.parser.DynamicContext;
import com.github.esbatis.parser.TokenHandler;
import com.github.esbatis.parser.TokenParser;
import com.github.esbatis.utils.MvelUtils;

import java.util.Map;

/**
 * @author jinzhong.zhang
 */
public class ForEachNode implements XmlNode {
    public static final String ITEM_PREFIX = "__foreach_";

    private final String collectionExpression;
    private final XmlNode contents;
    private final String open;
    private final String close;
    private final String separator;
    private final String item;
    private final String index;

    public ForEachNode(XmlNode contents, String collectionExpression, String index, String item, String open, String close, String separator) {
        this.collectionExpression = collectionExpression;
        this.contents = contents;
        this.open = open;
        this.close = close;
        this.separator = separator;
        this.index = index;
        this.item = item;
    }

    @Override
    public boolean apply(DynamicContext context) {
        Map<String, Object> bindings = context.getBindings();
        Iterable<?> iterable = MvelUtils.evalIterable(collectionExpression, bindings);
        if (!iterable.iterator().hasNext()) {
            return true;
        }
        boolean first = true;
        applyOpen(context);
        int i = 0;
        for (Object o : iterable) {
            DynamicContext oldContext = context;
            if (first || separator == null) {
                context = new PrefixedContext(context, "");
            } else {
                context = new PrefixedContext(context, separator);
            }
            int uniqueNumber = context.getUniqueNumber();
            if (o instanceof Map.Entry) {
                @SuppressWarnings("unchecked")
                Map.Entry<Object, Object> mapEntry = (Map.Entry<Object, Object>) o;
                applyIndex(context, mapEntry.getKey(), uniqueNumber);
                applyItem(context, mapEntry.getValue(), uniqueNumber);
            } else {
                applyIndex(context, i, uniqueNumber);
                applyItem(context, o, uniqueNumber);
            }
            contents.apply(new FilteredDynamicContext(context, index, item, uniqueNumber));
            if (first) {
                first = !((PrefixedContext) context).isPrefixApplied();
            }
            context = oldContext;
            i++;
        }
        applyClose(context);
        return true;
    }

    private void applyIndex(DynamicContext context, Object o, int i) {
        if (index != null) {
            context.bind(index, o);
            context.bind(itemizeItem(index, i), o);
        }
    }

    private void applyItem(DynamicContext context, Object o, int i) {
        if (item != null) {
            context.bind(item, o);
            context.bind(itemizeItem(item, i), o);
        }
    }

    private void applyOpen(DynamicContext context) {
        if (open != null) {
            context.appendString(open);
        }
    }

    private void applyClose(DynamicContext context) {
        if (close != null) {
            context.appendString(close);
        }
    }

    private static String itemizeItem(String item, int i) {
        return new StringBuilder(ITEM_PREFIX).append(item).append("_").append(i).toString();
    }

    private static class FilteredDynamicContext extends DynamicContext {
        private final DynamicContext delegate;
        private final int index;
        private final String itemIndex;
        private final String item;

        public FilteredDynamicContext(DynamicContext delegate, String itemIndex, String item, int i) {
            super(null);
            this.delegate = delegate;
            this.index = i;
            this.itemIndex = itemIndex;
            this.item = item;
        }

        @Override
        public Map<String, Object> getBindings() {
            return delegate.getBindings();
        }

        @Override
        public void bind(String name, Object value) {
            delegate.bind(name, value);
        }

        @Override
        public String getResult() {
            return delegate.getResult();
        }

        @Override
        public void appendString(String sql) {
            TokenParser parser = new TokenParser("${", "}", new TokenHandler() {
                @Override
                public String handleToken(String content) {
                    String newContent = content.replaceFirst("^\\s*" + item + "(?![^.,:\\s])", itemizeItem(item, index));
                    if (itemIndex != null && newContent.equals(content)) {
                        newContent = content.replaceFirst("^\\s*" + itemIndex + "(?![^.,:\\s])", itemizeItem(itemIndex, index));
                    }
                    return new StringBuilder("${").append(newContent).append("}").toString();
                }
            });

            delegate.appendString(parser.parse(sql));
        }

        @Override
        public int getUniqueNumber() {
            return delegate.getUniqueNumber();
        }

    }


    private class PrefixedContext extends DynamicContext {
        private final DynamicContext delegate;
        private final String prefix;
        private boolean prefixApplied;

        public PrefixedContext(DynamicContext delegate, String prefix) {
            super(null);
            this.delegate = delegate;
            this.prefix = prefix;
            this.prefixApplied = false;
        }

        public boolean isPrefixApplied() {
            return prefixApplied;
        }

        @Override
        public Map<String, Object> getBindings() {
            return delegate.getBindings();
        }

        @Override
        public void bind(String name, Object value) {
            delegate.bind(name, value);
        }

        @Override
        public void appendString(String sql) {
            if (!prefixApplied && sql != null && sql.trim().length() > 0) {
                delegate.appendString(prefix);
                prefixApplied = true;
            }
            delegate.appendString(sql);
        }

        @Override
        public String getResult() {
            return delegate.getResult();
        }

        @Override
        public int getUniqueNumber() {
            return delegate.getUniqueNumber();
        }
    }

}
