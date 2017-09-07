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

import com.github.esbatis.parser.tags.XmlNode;
import com.github.esbatis.session.Configuration;
import com.github.esbatis.session.MappedStatement;
import com.github.esbatis.utils.Resources;
import com.github.esbatis.utils.XMLNodeUtils;
import org.w3c.dom.Node;

/**
 * @author Clinton Begin
 */
public class XMLStatementParser extends BaseParser {

  private final Node context;
  private final String namespace;

  public XMLStatementParser(Configuration configuration, String namespace, Node context) {
    super(configuration);
    this.context = context;
    this.namespace = namespace;
  }

  public void parseStatementNode() {
    String commandType = XMLNodeUtils.getName(context);
    String id = XMLNodeUtils.getStringAttribute(context, "id");
    id = idForNamespace(id);
    String url = XMLNodeUtils.getStringAttribute(context, "url");
    String method = XMLNodeUtils.getStringAttribute(context, "method");

    Integer fetchSize = XMLNodeUtils.getIntAttribute(context, "fetchSize");
    Integer timeout = XMLNodeUtils.getIntAttribute(context, "timeout");
    String parameterType = XMLNodeUtils.getStringAttribute(context, "parameterType");
    Class<?> parameterTypeClass = resolveClass(parameterType);
    String resultMap = XMLNodeUtils.getStringAttribute(context, "resultMap");
    String resultType = XMLNodeUtils.getStringAttribute(context, "resultType");

    Class<?> resultTypeClass = resolveClass(resultType);
    String resultSetType = XMLNodeUtils.getStringAttribute(context, "resultSetType");


    // Parse the SQL (pre: <selectKey> and <include> were parsed and removed)
    XMLTagsParser builder = new XMLTagsParser(configuration, context);
    XmlNode bodyNode = builder.parseBodyNode();

    MappedStatement ms = new MappedStatement(configuration, commandType, id, url, method, bodyNode);
    configuration.addMappedStatement(ms);
  }

  private Class<?> resolveClass(String type) {
    try {
      if (type == null) {
        return null;
      }
      return Resources.classForName(type);
    } catch (ClassNotFoundException e) {
      throw new ParserException("Could not resolve type '" + type + "'.  Cause: " + e, e);
    }
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
