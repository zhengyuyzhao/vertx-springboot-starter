package com.zzy.vertx.core.handler.error;

import io.vertx.ext.web.RoutingContext;

public interface ErrorHandler {
  void handle(RoutingContext context, Throwable e);
}
