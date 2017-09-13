package com.github.esbatis.parser.nodes;

import com.github.esbatis.parser.DynamicContext;

import java.util.List;

/**
 * @author jinzhong.zhang
 */
public class MixedNode implements XmlNode {
    private final List<XmlNode> contents;

    public MixedNode(List<XmlNode> contents) {
        this.contents = contents;
    }

    @Override
    public boolean apply(DynamicContext context) {
        for (XmlNode sqlNode : contents) {
            sqlNode.apply(context);
        }
        return true;
    }
}
