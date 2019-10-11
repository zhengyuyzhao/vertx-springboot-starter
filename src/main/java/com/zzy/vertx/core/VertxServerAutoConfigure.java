package com.zzy.vertx.core;

import com.zzy.vertx.core.handler.VertxHandlerInterceptorAutoConfigure;
import com.zzy.vertx.core.router.VertxRouterAutoConfigure;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({VertxHandlerInterceptorAutoConfigure.class, VertxRouterAutoConfigure.class, VertxServer.class,})
public class VertxServerAutoConfigure {
}
