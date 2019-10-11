package com.zzy.vertx.core.handler;

import io.vertx.ext.web.RoutingContext;
import org.springframework.lang.Nullable;

public interface VertxHandlerInterceptor {
  default boolean preHandle(RoutingContext context, Object handler) throws Exception {
    return true;
  }

  default void postHandle(RoutingContext context, Object handler, @Nullable Object result) throws Exception {
  }
}
