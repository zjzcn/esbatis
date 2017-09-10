package com.github.esbatis.client;

/**
 * @author jinzhong.zhang
 */
public class RestException extends RuntimeException {

  public RestException() {
    super();
  }

  public RestException(String message) {
    super(message);
  }

  public RestException(String message, Throwable cause) {
    super(message, cause);
  }

  public RestException(Throwable cause) {
    super(cause);
  }
}
