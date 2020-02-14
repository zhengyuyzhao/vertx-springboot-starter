package com.zzy.vertx.core.handler.method;

import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Method;

public interface MethodAnnotaionHandler {
  boolean handle(RoutingContext context, Method method, Object bean);
}
