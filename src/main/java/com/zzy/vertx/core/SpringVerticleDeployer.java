package com.zzy.vertx.core;

import com.zzy.vertx.config.VertxConfig;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.spi.VerticleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextStoppedEvent;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnBean(SpringVerticleFactory.class)
public class SpringVerticleDeployer {
  private static final Logger logger = LoggerFactory.getLogger(SpringVerticleDeployer.class);

  @Autowired
  private VerticleFactory verticleFactory;

  @Autowired
  private VertxConfig vertxConfig;

  @Autowired
  private Vertx vertx;


  @PostConstruct
  public void afterPropertiesSet() throws Exception {
    vertx.registerVerticleFactory(verticleFactory);
    // Scale the verticles on cores: create 4 instances during the deployment
    DeploymentOptions options = new DeploymentOptions()
      .setHa(true)
      .setWorker(false)
      .setWorkerPoolSize(vertxConfig.getWorkPoolSize())
      .setInstances(vertxConfig.getInstance());
    Promise promise = Promise.promise();
    vertx.deployVerticle(verticleFactory.prefix() + ":" + VertxServer.class.getName(), options, stringAsyncResult -> {
      if (stringAsyncResult.succeeded()) {
        logger.info("----------vertx success start at port : {}", vertxConfig.getPort());
        promise.complete(stringAsyncResult.result());
      } else {
        promise.fail(stringAsyncResult.cause());
        stringAsyncResult.cause().fillInStackTrace().printStackTrace();
        System.exit(1);
      }
    });
  }

}
