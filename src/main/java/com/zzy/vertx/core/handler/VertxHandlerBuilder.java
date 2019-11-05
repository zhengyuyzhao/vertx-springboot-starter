package com.zzy.vertx.core.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Method;

public interface VertxHandlerBuilder {
  Handler<RoutingContext> build(Method method, Object bean, boolean isAsync);
}
