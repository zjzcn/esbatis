package com.github.esbatis.parser;

/**
 * @author jinzhong.zhang
 */
public class ParserException extends RuntimeException {

    public ParserException() {
        super();
    }

    public ParserException(String message) {
        super(message);
    }

    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParserException(Throwable cause) {
        super(cause);
    }
}
