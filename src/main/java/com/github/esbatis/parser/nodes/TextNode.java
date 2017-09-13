package com.github.esbatis.parser.nodes;

import com.github.esbatis.parser.DynamicContext;
import com.github.esbatis.parser.PlaceholderParser;

/**
 * @author jinzhong.zhang
 */
public class TextNode implements XmlNode {
    private final String text;

    public TextNode(String text) {
        this.text = text;
    }

    @Override
    public boolean apply(DynamicContext context) {
        PlaceholderParser parser = new PlaceholderParser();
        context.appendString(parser.parse(text, context.getBindings()));
        return true;
    }

}