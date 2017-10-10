package com.github.esbatis.executor;

import com.github.esbatis.mapper.CommandType;

import java.util.LinkedHashMap;
import java.util.Map;

public class FilterContext {

    private String statement;
    private CommandType commandType;

    private String httpHost;
    private String httpUrl;
    private String httpMethod;
    private String renderedHttpUrl;
    private int httpStatusCode;

    private String methodName;

    private Throwable exception;

    private Map<String, Object> variables = new LinkedHashMap<>();

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public String getHttpHost() {
        return httpHost;
    }

    public void setHttpHost(String httpHost) {
        this.httpHost = httpHost;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getRenderedHttpUrl() {
        return renderedHttpUrl;
    }

    public void setRenderedHttpUrl(String renderedHttpUrl) {
        this.renderedHttpUrl = renderedHttpUrl;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}
