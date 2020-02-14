package com.zzy.vertx.core.webconfig;

import com.zzy.vertx.core.handler.DefaultHandlerBuilder;
import com.zzy.vertx.core.handler.VertxHandlerBuilder;
import com.zzy.vertx.core.handler.VertxHandlerInterceptorManager;
import com.zzy.vertx.core.message.MessageConvertManager;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;

public class VertxWebConfigSupport implements ApplicationContextAware {

  @Autowired
  private Vertx vertx;

  private ApplicationContext applicationContext;

  @Bean
  @Lazy
  @ConditionalOnMissingBean
  @Order()
  public Router routerBean() {
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    return router;
  }

  @Bean
  @Lazy
  @ConditionalOnMissingBean
  @Order()
  public VertxHandlerBuilder vertxHandlerBuilder() {
    return new DefaultHandlerBuilder();
  }


  protected void addInterceptors(VertxHandlerInterceptorManager interceptorManager) {
  }

  protected void addMessageConverts(MessageConvertManager convertManager) {
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
