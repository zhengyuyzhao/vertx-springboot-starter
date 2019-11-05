package com.zzy.vertx.core.handler;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class VertxHandlerInterceptorProcessor implements BeanPostProcessor {

  @Autowired
  private VertxHandlerInterceptorManager vertxHandlerInterceptorManager;

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof VertxHandlerInterceptor) {
      vertxHandlerInterceptorManager.addInterceptor((VertxHandlerInterceptor) bean);
    }
    return bean;
  }
}
