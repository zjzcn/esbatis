package com.github.esbatis.client;

/**
 * @author jinzhong.zhang
 */
public final class HttpRequest {

    private String url;
    private String method;
    private String body;

    public HttpRequest(String url, String method, String body) {
        this.url = url;
        this.method = method;
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "url=" + url +
                ", method=" + method +
                ", body=" + body +
                '}';
    }
}