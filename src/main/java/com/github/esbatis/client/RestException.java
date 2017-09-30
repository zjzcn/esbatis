package com.github.esbatis.client;

/**
 * @author jinzhong.zhang
 */
public class RestException extends RuntimeException {

    private String host;

    public RestException(String host) {
        super();
        this.host = host;
    }

    public RestException(String host, String message) {
        super(message);
        this.host = host;
    }

    public RestException(String host, Throwable cause) {
        super(cause);
        this.host = host;
    }

    public RestException(String host, String message, Throwable cause) {
        super(message, cause);
        this.host = host;
    }

    public String getHost() {
        return host;
    }

}
