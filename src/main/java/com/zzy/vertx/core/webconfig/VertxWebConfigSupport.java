package com.zzy.vertx.core.webconfig;

import com.zzy.vertx.core.handler.VertxHandlerInterceptorManager;
import com.zzy.vertx.core.handler.VertxHandlerInterceptorProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class VertxWebConfigSupport {

  @Bean
  @ConditionalOnMissingBean
  public VertxHandlerInterceptorManager vertxHandlerInterceptorManager() {
    VertxHandlerInterceptorManager manager = new VertxHandlerInterceptorManager();
    addInterceptors(manager);
    return manager;
  }

  @Bean
  @ConditionalOnMissingBean
  public VertxHandlerInterceptorProcessor vertxHandlerInterceptorProcessor() {
    return new VertxHandlerInterceptorProcessor();
  }

  protected void addInterceptors(VertxHandlerInterceptorManager interceptorManager) {
  }
}
