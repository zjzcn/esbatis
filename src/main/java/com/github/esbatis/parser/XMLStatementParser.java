package com.github.esbatis.parser;

import com.github.esbatis.mapper.MappedStatement;
import com.github.esbatis.mapper.MapperFactory;
import com.github.esbatis.parser.nodes.XmlNode;
import com.github.esbatis.utils.XMLNodeUtils;
import org.w3c.dom.Node;

/**
 * @author jinzhong.zhang
 */
public class XMLStatementParser {

  private MapperFactory mapperFactory;
  private final Node node;
  private final String namespace;

  public XMLStatementParser(MapperFactory mapperFactory, String namespace, Node node) {
    this.mapperFactory = mapperFactory;
    this.node = node;
    this.namespace = namespace;
  }

  public void parseStatementNode() {
    String commandType = XMLNodeUtils.getName(node);
    String id = XMLNodeUtils.getStringAttribute(node, "id");
    id = idForNamespace(id);
    String url = XMLNodeUtils.getStringAttribute(node, "url");
    String method = XMLNodeUtils.getStringAttribute(node, "method");

    Integer timeout = XMLNodeUtils.getIntAttribute(node, "timeout");

    // Parse the SQL (pre: <selectKey> and <include> were parsed and removed)
    XMLNodeParser builder = new XMLNodeParser(node);
    XmlNode bodyNode = builder.parseBodyNode();

    MappedStatement ms = new MappedStatement(commandType, id, url, method, timeout, bodyNode);
    mapperFactory.addMappedStatement(ms);
  }

  private String idForNamespace(String id) {
    // is it qualified with this namespace yet?
    if (id.startsWith(namespace + ".")) {
      return id;
    }
    if (id.contains(".")) {
      throw new ParserException("Dots are not allowed in element names, please remove it from " + id);
    }
    return namespace + "." + id;
  }
}
