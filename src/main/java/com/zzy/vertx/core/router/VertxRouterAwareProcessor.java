package com.zzy.vertx.core.router;

import io.vertx.ext.web.Router;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(Router.class)
public class VertxRouterAwareProcessor implements BeanPostProcessor {
  @Autowired
  private Router router;
  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    if(bean instanceof VertxRouterAware){
      ((VertxRouterAware) bean).setRouter(router);
    }
    return bean;
  }
}
