package com.github.esbatis.parser.nodes;

import com.github.esbatis.parser.DynamicContext;

import java.util.List;

/**
 * @author jinzhong.zhang
 */
public class ChooseNode implements XmlNode {
    private final XmlNode defaultNode;
    private final List<XmlNode> ifSqlNodes;

    public ChooseNode(List<XmlNode> ifNodes, XmlNode defaultNode) {
        this.ifSqlNodes = ifNodes;
        this.defaultNode = defaultNode;
    }

    @Override
    public boolean apply(DynamicContext context) {
        for (XmlNode sqlNode : ifSqlNodes) {
            if (sqlNode.apply(context)) {
                return true;
            }
        }
        if (defaultNode != null) {
            defaultNode.apply(context);
            return true;
        }
        return false;
    }
}
