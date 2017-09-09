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

import com.github.esbatis.exceptions.ParserException;
import com.github.esbatis.parser.tags.*;
import com.github.esbatis.config.Configuration;
import com.github.esbatis.utils.XMLNodeUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Clinton Begin
 */
public class XMLTagsParser extends BaseParser {

  private final Map<String, NodeHandler> nodeHandlers = new HashMap<String, NodeHandler>();

  private final Node context;

  public XMLTagsParser(Configuration configuration, Node context) {
    super(configuration);
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
        String data = XMLNodeUtils.getStringBody(child, "");
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
      String prefix = XMLNodeUtils.getStringAttribute(node,"prefix");
      String prefixOverrides = XMLNodeUtils.getStringAttribute(node,"prefixOverrides");
      String suffix = XMLNodeUtils.getStringAttribute(node, "suffix");
      String suffixOverrides = XMLNodeUtils.getStringAttribute(node, "suffixOverrides");
      TrimNode trim = new TrimNode(configuration, mixedSqlNode, prefix, prefixOverrides, suffix, suffixOverrides);
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
      String collection = XMLNodeUtils.getStringAttribute(node,"collection");
      String item = XMLNodeUtils.getStringAttribute(node, "item");
      String index = XMLNodeUtils.getStringAttribute(node, "index");
      String open = XMLNodeUtils.getStringAttribute(node, "open");
      String close = XMLNodeUtils.getStringAttribute(node, "close");
      String separator = XMLNodeUtils.getStringAttribute(node, "separator");
      ForEachNode forEachSqlNode = new ForEachNode(configuration, mixedSqlNode, collection, index, item, open, close, separator);
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
      String test = XMLNodeUtils.getStringAttribute(node, "test");
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
      List<Node> children = XMLNodeUtils.getChildren(chooseSqlNode);
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
