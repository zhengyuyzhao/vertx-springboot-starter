package com.zzy.vertx.core.router;

import io.vertx.ext.web.Router;

public interface VertxRouterAware {
  default void setRouter(Router router){};
}
