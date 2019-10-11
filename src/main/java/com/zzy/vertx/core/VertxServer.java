package com.zzy.vertx.core;

import com.zzy.vertx.config.VertxConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.web.Router;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(Router.class)
@EnableConfigurationProperties(VertxConfig.class)
public class VertxServer extends AbstractVerticle implements ApplicationContextAware, InitializingBean {

  @Autowired
  private VertxConfig vertxConfig;

  @Autowired
  private Router router;

  private ApplicationContext springContext;


  @Override
  public void start() throws Exception {
    super.start();
    vertx.createHttpServer().requestHandler(router).listen(vertxConfig.getPort());
  }

  @Override
  public void stop() throws Exception {
    super.stop();
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.springContext = applicationContext;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    VertxOptions vertxOptions = new VertxOptions();
    vertxOptions.setWorkerPoolSize(vertxConfig.getWorkPoolSize())
      .setEventLoopPoolSize(vertxConfig.getInstance());
    Vertx.vertx().deployVerticle(this);
  }
}
