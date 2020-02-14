package com.zzy.vertx.core.handler.error;

import io.vertx.ext.web.RoutingContext;
import org.springframework.beans.TypeMismatchException;

import javax.validation.ValidationException;

public class DefaultErrorHandler implements ErrorHandler {
  @Override
  public void handle(RoutingContext ctx, Throwable e) {
    if (e instanceof TypeMismatchException || e instanceof ValidationException) {
      ctx.fail(400, e);
    } else {
      ctx.fail(500, e);
    }
  }
}
