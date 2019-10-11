package com.zzy.vertx.core.handler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(VertxHandlerInterceptorProcessor.class)
public class VertxHandlerInterceptorAutoConfigure {

  @Bean
  public VertxHandlerInterceptorManager vertxHandlerInterceptorManager(){
    return new VertxHandlerInterceptorManager();
  }
}
