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

import com.github.esbatis.config.Configuration;
import com.github.esbatis.exceptions.ParserException;
import com.github.esbatis.utils.XMLNodeUtils;
import org.w3c.dom.Node;

import java.io.InputStream;
import java.util.List;

/**
 * @author Clinton Begin
 */
public class XMLMapperParser extends BaseParser {

  private final XPathParser parser;
  private String namespace;

  public XMLMapperParser(InputStream inputStream, Configuration configuration) {
    this(configuration, new XPathParser(inputStream, new XMLMapperEntityResolver()));
  }

  public XMLMapperParser(Configuration configuration, XPathParser parser) {
    super(configuration);
    this.parser = parser;
  }

  public void parse() {
    configurationElement(parser.evalNode("/mapper"));
  }

  private void configurationElement(Node mapperNode) {
    try {
      String namespace = XMLNodeUtils.getStringAttribute(mapperNode, "namespace");
      if (namespace == null || namespace.equals("")) {
        throw new ParserException("Mapper's namespace cannot be empty");
      }
      this.namespace = namespace;
//      resultMapElements(context.evalNodes("/mapper/resultMap"));
      buildStatementFromContext(parser.evalNodes(mapperNode,
              "index|update|delete|get|search|delete_by_query|update_by_query|mget|bulk"));
    } catch (Exception e) {
      throw new ParserException("Error parsing Mapper XML. Cause: " + e, e);
    }
  }

  private void buildStatementFromContext(List<Node> list) {
    for (Node context : list) {
      XMLStatementParser statementParser = new XMLStatementParser(configuration, namespace, context);
      statementParser.parseStatementNode();
    }
  }

//  private void bindMapperForNamespace() {
//    if (namespace != null) {
//      Class<?> boundType = null;
//      try {
//        boundType = Resources.classForName(namespace);
//      } catch (ClassNotFoundException e) {
//        //ignore, bound type is not required
//      }
//      if (boundType != null) {
//        if (!configuration.hasMapper(boundType)) {
//          configuration.addMapper(boundType);
//        }
//      }
//    }
//  }

}
