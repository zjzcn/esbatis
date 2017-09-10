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

import com.github.esbatis.mapper.MappedStatement;
import com.github.esbatis.mapper.MapperFactory;
import com.github.esbatis.parser.nodes.XmlNode;
import com.github.esbatis.utils.XMLNodeUtils;
import org.w3c.dom.Node;

/**
 * @author
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
