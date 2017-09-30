package com.github.esbatis.client;

/**
 * @author jinzhong.zhang
 */
public final class HttpResponse {

    private String host;
    private int code;
    private String body;

    public HttpResponse(String host, int code, String body) {
        this.host = host;
        this.code = code;
        this.body = body;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "host=" + host +
                ", code=" + code +
                ", body=" + body +
                '}';
    }
}