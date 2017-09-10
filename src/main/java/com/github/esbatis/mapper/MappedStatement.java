package com.github.esbatis.mapper;

import com.github.esbatis.parser.DynamicContext;
import com.github.esbatis.parser.PlaceholderParser;
import com.github.esbatis.parser.nodes.XmlNode;
import com.github.esbatis.proxy.MapperMethod;

import java.util.Locale;
import java.util.Map;

/**
 * @author jinzhong.zhang
 */
public final class MappedStatement {

  private String id;
  private CommandType commandType;

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
    return parser.parse(body, context.getBindings());
  }

  public String renderHttpUrl(Map<String, Object> parameterMap) {
    PlaceholderParser parser = new PlaceholderParser();
    //parse #{} and ${}
    return parser.parse(this.httpUrl, parameterMap);
  }

}
