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
package com.github.esbatis.session;

import com.github.esbatis.parser.DynamicContext;
import com.github.esbatis.parser.ParameterParser;
import com.github.esbatis.parser.tags.XmlNode;
import com.github.esbatis.proxy.MapperMethod;

import java.util.Locale;

/**
 * @author Clinton Begin
 */
public final class MappedStatement {

  private Configuration configuration;
  private String resource;
  private String id;
  private CommandType commandType;
  private String url;
  private String method;
  private Integer fetchSize;
  private Integer timeout;
  private String[] resultSets;

  private XmlNode bodyNode;
  private MapperMethod mapperMethod;

  public MappedStatement(Configuration configuration, String commandType, String id, String url, String method, XmlNode bodyNode) {
    this.configuration = configuration;
    this.id = id;
    this.commandType = CommandType.valueOf(commandType.toUpperCase(Locale.ENGLISH));
    this.url = url;
    this.method = method.toUpperCase(Locale.ENGLISH);
    this.bodyNode = bodyNode;
  }

  public String getResource() {
    return resource;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public CommandType getCommandType() {
    return commandType;
  }

  public void setCommandType(CommandType commandType) {
    this.commandType = commandType;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Integer getFetchSize() {
    return fetchSize;
  }

  public void setFetchSize(Integer fetchSize) {
    this.fetchSize = fetchSize;
  }

  public Integer getTimeout() {
    return timeout;
  }

  public void setTimeout(Integer timeout) {
    this.timeout = timeout;
  }

  public String[] getResultSets() {
    return resultSets;
  }

  public void setResultSets(String[] resultSets) {
    this.resultSets = resultSets;
  }

  public XmlNode getBodyNode() {
    return bodyNode;
  }

  public MapperMethod getMapperMethod() {
    return mapperMethod;
  }

  public void setMapperMethod(MapperMethod mapperMethod) {
    this.mapperMethod = mapperMethod;
  }

  public BoundHttp getBoundHttp(Object parameterObject) {
    DynamicContext context = new DynamicContext(configuration, parameterObject);
    //parse xml tags
    bodyNode.apply(context);
    String body = context.getResult();
    ParameterParser parser = new ParameterParser();
    //parse #{}
    body = parser.parse(body, context.getBindings());
    String url = parser.parse(this.url, context.getBindings());

    return new BoundHttp(url, method, body);
  }

}
