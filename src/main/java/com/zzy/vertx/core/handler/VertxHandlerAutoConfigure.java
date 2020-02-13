package com.zzy.vertx.core.handler;

import com.zzy.vertx.core.handler.param.*;
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

  @Bean
  @ConditionalOnMissingBean
  public ParamTransferManager paramTransferManager() {
    ParamTransferManager transferManager = new ParamTransferManager();
    transferManager.addHandler(new RequestParamTransferHandler());
    transferManager.addHandler(new PathVariableTransferHandler());
    transferManager.addHandler(new RequestBodyTransferHandler());
    transferManager.addHandler(new DateTimeFormatTransferHandler());
    transferManager.addHandler(new RoutingContextTransferHandler());
    transferManager.addHandler(new ParamNameTransferHandler());
    return transferManager;
  }

}
