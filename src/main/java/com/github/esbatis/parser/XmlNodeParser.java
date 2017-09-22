package com.github.esbatis.parser;

import com.github.esbatis.parser.nodes.*;
import com.github.esbatis.utils.XmlNodeUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jinzhong.zhang
 */
public class XmlNodeParser {

    private final Map<String, NodeHandler> nodeHandlers = new HashMap<>();

    private final Node context;

    public XmlNodeParser(Node context) {
        this.context = context;

        nodeHandlers.put("trim", new TrimHandler());
        nodeHandlers.put("foreach", new ForEachHandler());
        nodeHandlers.put("if", new IfHandler());
        nodeHandlers.put("choose", new ChooseHandler());
        nodeHandlers.put("when", new IfHandler());
        nodeHandlers.put("otherwise", new OtherwiseHandler());
    }

    public XmlNode parseBodyNode() {
        List<XmlNode> contents = parseDynamicTags(context);
        return new MixedNode(contents);
    }

    List<XmlNode> parseDynamicTags(Node node) {
        List<XmlNode> contents = new ArrayList<XmlNode>();
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.CDATA_SECTION_NODE || child.getNodeType() == Node.TEXT_NODE) {
                String data = XmlNodeUtils.getStringBody(child, "");
                TextNode textSqlNode = new TextNode(data);
                contents.add(textSqlNode);
            } else if (child.getNodeType() == Node.ELEMENT_NODE) { // issue #628
                String nodeName = child.getNodeName();
                NodeHandler handler = nodeHandlers(nodeName);
                if (handler == null) {
                    throw new ParserException("Unknown element <" + nodeName + "> in SQL statement.");
                }
                handler.handleNode(child, contents);
            }
        }
        return contents;
    }

    private NodeHandler nodeHandlers(String nodeName) {
        return nodeHandlers.get(nodeName);
    }

    private interface NodeHandler {
        void handleNode(Node node, List<XmlNode> targetContents);
    }

    private class TrimHandler implements NodeHandler {
        public TrimHandler() {
            // Prevent Synthetic Access
        }

        @Override
        public void handleNode(Node node, List<XmlNode> targetContents) {
            List<XmlNode> contents = parseDynamicTags(node);
            MixedNode mixedSqlNode = new MixedNode(contents);
            String prefix = XmlNodeUtils.getStringAttribute(node, "prefix");
            String prefixOverrides = XmlNodeUtils.getStringAttribute(node, "prefixOverrides");
            String suffix = XmlNodeUtils.getStringAttribute(node, "suffix");
            String suffixOverrides = XmlNodeUtils.getStringAttribute(node, "suffixOverrides");
            TrimNode trim = new TrimNode(mixedSqlNode, prefix, prefixOverrides, suffix, suffixOverrides);
            targetContents.add(trim);
        }
    }

    private class ForEachHandler implements NodeHandler {
        public ForEachHandler() {
            // Prevent Synthetic Access
        }

        @Override
        public void handleNode(Node node, List<XmlNode> targetContents) {
            List<XmlNode> contents = parseDynamicTags(node);
            MixedNode mixedSqlNode = new MixedNode(contents);
            String collection = XmlNodeUtils.getStringAttribute(node, "collection");
            String item = XmlNodeUtils.getStringAttribute(node, "item");
            String index = XmlNodeUtils.getStringAttribute(node, "index");
            String open = XmlNodeUtils.getStringAttribute(node, "open");
            String close = XmlNodeUtils.getStringAttribute(node, "close");
            String separator = XmlNodeUtils.getStringAttribute(node, "separator");
            ForEachNode forEachSqlNode = new ForEachNode(mixedSqlNode, collection, index, item, open, close, separator);
            targetContents.add(forEachSqlNode);
        }
    }

    private class IfHandler implements NodeHandler {
        public IfHandler() {
            // Prevent Synthetic Access
        }

        @Override
        public void handleNode(Node node, List<XmlNode> targetContents) {
            List<XmlNode> contents = parseDynamicTags(node);
            MixedNode mixedSqlNode = new MixedNode(contents);
            String test = XmlNodeUtils.getStringAttribute(node, "test");
            IfNode ifSqlNode = new IfNode(mixedSqlNode, test);
            targetContents.add(ifSqlNode);
        }
    }

    private class OtherwiseHandler implements NodeHandler {
        public OtherwiseHandler() {
            // Prevent Synthetic Access
        }

        @Override
        public void handleNode(Node node, List<XmlNode> targetContents) {
            List<XmlNode> contents = parseDynamicTags(node);
            MixedNode mixedSqlNode = new MixedNode(contents);
            targetContents.add(mixedSqlNode);
        }
    }

    private class ChooseHandler implements NodeHandler {
        public ChooseHandler() {
            // Prevent Synthetic Access
        }

        @Override
        public void handleNode(Node node, List<XmlNode> targetContents) {
            List<XmlNode> whenSqlNodes = new ArrayList<XmlNode>();
            List<XmlNode> otherwiseSqlNodes = new ArrayList<XmlNode>();
            handleWhenOtherwiseNodes(node, whenSqlNodes, otherwiseSqlNodes);
            XmlNode defaultSqlNode = getDefaultSqlNode(otherwiseSqlNodes);
            ChooseNode chooseSqlNode = new ChooseNode(whenSqlNodes, defaultSqlNode);
            targetContents.add(chooseSqlNode);
        }

        private void handleWhenOtherwiseNodes(Node chooseSqlNode, List<XmlNode> ifSqlNodes, List<XmlNode> defaultSqlNodes) {
            List<Node> children = XmlNodeUtils.getChildren(chooseSqlNode);
            for (Node child : children) {
                String nodeName = child.getNodeName();
                NodeHandler handler = nodeHandlers(nodeName);
                if (handler instanceof IfHandler) {
                    handler.handleNode(child, ifSqlNodes);
                } else if (handler instanceof OtherwiseHandler) {
                    handler.handleNode(child, defaultSqlNodes);
                }
            }
        }

        private XmlNode getDefaultSqlNode(List<XmlNode> defaultSqlNodes) {
            XmlNode defaultSqlNode = null;
            if (defaultSqlNodes.size() == 1) {
                defaultSqlNode = defaultSqlNodes.get(0);
            } else if (defaultSqlNodes.size() > 1) {
                throw new ParserException("Too many default (otherwise) elements in choose statement.");
            }
            return defaultSqlNode;
        }
    }

}
