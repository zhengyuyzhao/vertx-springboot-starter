package com.zzy.vertx.core.deploy;

import com.zzy.vertx.config.VertxConfig;
import io.vertx.core.Verticle;
import io.vertx.core.spi.VerticleFactory;
import io.vertx.ext.web.Router;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(Router.class)
@EnableConfigurationProperties(VertxConfig.class)
public class SpringVerticleFactory implements VerticleFactory, ApplicationContextAware {
  private ApplicationContext applicationContext;

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

}
