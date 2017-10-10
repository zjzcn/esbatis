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

    private String statement;
    private CommandType commandType;

    private String httpUrl;
    private String httpMethod;
    private XmlNode bodyNode;

    private MapperMethod mapperMethod;

    public MappedStatement(String commandType, String statement, String httpUrl, String httpMethod, XmlNode bodyNode) {
        this.statement = statement;
        this.commandType = CommandType.valueOf(commandType.toUpperCase(Locale.ENGLISH));
        this.httpUrl = httpUrl;
        this.httpMethod = httpMethod.toUpperCase(Locale.ENGLISH);
        this.bodyNode = bodyNode;
    }

    public String getStatement() {
        return statement;
    }

    public CommandType getCommandType() {
        return commandType;
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
        //parse xml nodes
        bodyNode.apply(context);

        String body = context.getResult();
        PlaceholderParser parser = new PlaceholderParser();
        try {
            //parse ${}
            return parser.parse(body, context.getBindings());
        } catch (Exception e) {
            throw new MapperException("Parse statement[" + statement + "] body error.", e);
        }
    }

    public String renderHttpUrl(Map<String, Object> parameterMap) {
        PlaceholderParser parser = new PlaceholderParser();
        try {
            //parse ${}
            return parser.parse(this.httpUrl, parameterMap);
        } catch (Exception e) {
            throw new MapperException("Parse statement[" + statement + "] url error.", e);
        }
    }

    public String getHttpUrl() {
        return httpUrl;
    }
}
