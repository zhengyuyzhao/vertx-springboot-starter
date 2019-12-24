package com.zzy.vertx.core.handler;

import com.zzy.vertx.core.message.MessageConvertManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VertxHandlerAutoConfigure {

  @Bean
  @ConditionalOnMissingBean
  public VertxHandlerInterceptorManager vertxHandlerInterceptorManager() {
    VertxHandlerInterceptorManager manager = new VertxHandlerInterceptorManager();
    return manager;
  }

  @Bean
  @ConditionalOnMissingBean
  public MessageConvertManager messageOutConvertManager() {
    MessageConvertManager convertManager = new MessageConvertManager();
    return convertManager;
  }

}
