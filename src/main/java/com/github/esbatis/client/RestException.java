package com.github.esbatis.client;

/**
 * @author jinzhong.zhang
 */
public class RestException extends RuntimeException {

  private static final long serialVersionUID = 3833184690240265047L;

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
