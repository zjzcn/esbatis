package com.github.esbatis.handler;

/**
 * @author jinzhong.zhang
 */
public class HandlerException extends RuntimeException {
  private static final long serialVersionUID = -176685891441325943L;

  public HandlerException() {
    super();
  }

  public HandlerException(String message) {
    super(message);
  }

  public HandlerException(String message, Throwable cause) {
    super(message, cause);
  }

  public HandlerException(Throwable cause) {
    super(cause);
  }
}
