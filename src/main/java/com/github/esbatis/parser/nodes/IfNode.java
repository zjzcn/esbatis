package com.github.esbatis.parser.nodes;

import com.github.esbatis.parser.DynamicContext;
import com.github.esbatis.utils.MVELUtils;

/**
 * @author jinzhong.zhang
 */
public class IfNode implements XmlNode {
    private final String test;
    private final XmlNode contents;

    public IfNode(XmlNode contents, String test) {
        this.test = test;
        this.contents = contents;
    }

    @Override
    public boolean apply(DynamicContext context) {
        if (MVELUtils.evalBoolean(test, context.getBindings())) {
            contents.apply(context);
            return true;
        }
        return false;
    }

}
