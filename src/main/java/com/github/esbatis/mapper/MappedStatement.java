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
package com.github.esbatis.mapper;

import com.github.esbatis.parser.DynamicContext;
import com.github.esbatis.parser.PlaceholderParser;
import com.github.esbatis.parser.nodes.XmlNode;
import com.github.esbatis.proxy.MapperMethod;

import java.util.Locale;
import java.util.Map;

/**
 * @author
 */
public final class MappedStatement {

  public static final Integer DEFAULT_HTTP_TIMEOUT = 300000;

  private String id;
  private CommandType commandType;
  private Integer timeout;

  private String httpUrl;
  private String httpMethod;
  private XmlNode bodyNode;

  private MapperMethod mapperMethod;

  public MappedStatement(String commandType, String id, String httpUrl, String httpMethod, Integer timeout, XmlNode bodyNode) {
    this.id = id;
    this.commandType = CommandType.valueOf(commandType.toUpperCase(Locale.ENGLISH));
    this.httpUrl = httpUrl;
    this.httpMethod = httpMethod.toUpperCase(Locale.ENGLISH);
    this.bodyNode = bodyNode;
    this.timeout = (timeout == null || timeout <=0 ? DEFAULT_HTTP_TIMEOUT : timeout);
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

  public String getHttpMethod() {
    return httpMethod;
  }


  public Integer getTimeout() {
    return timeout;
  }

  public void setTimeout(Integer timeout) {
    this.timeout = timeout;
  }

  public MapperMethod getMapperMethod() {
    return mapperMethod;
  }

  public void setMapperMethod(MapperMethod mapperMethod) {
    this.mapperMethod = mapperMethod;
  }

  public String renderHttpBody(Map<String, Object> parameterMap) {
    DynamicContext context = new DynamicContext(parameterMap);
    //parse xml tags
    bodyNode.apply(context);
    String body = context.getResult();
    PlaceholderParser parser = new PlaceholderParser();
    //parse #{} and ${}
    body = parser.parse(body, context.getBindings());

    return body;
  }

  public String renderHttpUrl(Map<String, Object> parameterMap) {
    PlaceholderParser parser = new PlaceholderParser();
    //parse #{} and ${}
    String renderedUrl = parser.parse(this.httpUrl, parameterMap);

    return renderedUrl;
  }

}
