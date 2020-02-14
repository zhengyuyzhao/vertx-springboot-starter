package com.zzy.vertx.core.handler;

import com.zzy.vertx.core.handler.error.ErrorHandler;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Method;
import java.util.Map;

public interface VertxHandlerBuilder {
  Handler<RoutingContext> build(Method method, Object bean, boolean isAsync);
  Map<Class<? extends Throwable>, ErrorHandler> buildExceptionHandler(Object bean);
}
