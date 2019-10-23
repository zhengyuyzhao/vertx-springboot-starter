package com.zzy.vertx.core;

import com.zzy.vertx.config.VertxConfig;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.spi.VerticleFactory;
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
public class SpringVerticleFactory implements VerticleFactory, ApplicationContextAware, InitializingBean {

  private ApplicationContext applicationContext;

  @Autowired
  private VertxConfig vertxConfig;

  @Autowired
  private Vertx vertx;

  @Override
  public String prefix() {
    return "spring-vertx";
  }

  @Override
  public boolean blockingCreate() {
    // Usually verticle instantiation is fast but since our verticles are Spring Beans,
    // they might depend on other beans/resources which are slow to build/lookup.
    return true;
  }

  @Override
  public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
    // Our convention in this example is to give the class name as verticle name
    String clazz = VerticleFactory.removePrefix(verticleName);
    return (Verticle) applicationContext.getBean(Class.forName(clazz));
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    VerticleFactory verticleFactory = applicationContext.getBean(SpringVerticleFactory.class);

    // The verticle factory is registered manually because it is created by the Spring container
    vertx.registerVerticleFactory(verticleFactory);

    // Scale the verticles on cores: create 4 instances during the deployment
    DeploymentOptions options = new DeploymentOptions()
      .setHa(true)
      .setWorker(false)
      .setWorkerPoolSize(vertxConfig.getWorkPoolSize())
      .setInstances(vertxConfig.getInstance());
    vertx.deployVerticle(verticleFactory.prefix() + ":" + VertxServer.class.getName(), options);
  }
}
