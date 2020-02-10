package com.zzy.vertx.core.webconfig;

import com.zzy.vertx.core.handler.VertxHandlerInterceptor;
import com.zzy.vertx.core.handler.VertxHandlerInterceptorManager;
import com.zzy.vertx.core.message.JsonMessageConvert;
import com.zzy.vertx.core.message.MessageConvertManager;
import com.zzy.vertx.core.message.StringMessageConvert;
import com.zzy.vertx.core.message.XmlMessageConvert;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;

import java.util.Map;

public class VertxWebConfigInit implements SmartLifecycle, ApplicationContextAware {
  private ApplicationContext applicationContext;
  @Autowired
  private VertxHandlerInterceptorManager vertxHandlerInterceptorManager;

  @Autowired
  private MessageConvertManager messageConvertManager;
  private boolean running;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public void start() {
    running = true;
    Map<String, VertxHandlerInterceptor> intercepters = this.applicationContext.getBeansOfType(VertxHandlerInterceptor.class);
    for(VertxHandlerInterceptor interceptor: intercepters.values()){
      vertxHandlerInterceptorManager.addInterceptor(interceptor);
    }
    Map<String, VertxWebConfigSupport> webConfigs = this.applicationContext.getBeansOfType(VertxWebConfigSupport.class);
    for(VertxWebConfigSupport support: webConfigs.values()){
      support.addInterceptors(vertxHandlerInterceptorManager);
      support.addMessageConverts(messageConvertManager);
    }
    messageConvertManager.addMessageConverts(new StringMessageConvert());
    messageConvertManager.addMessageConverts(new JsonMessageConvert());
    messageConvertManager.addMessageConverts(new XmlMessageConvert());
    stop();
  }

  @Override
  public void stop() {
    running = false;
  }

  @Override
  public boolean isRunning() {
    return running;
  }

  @Override
  public boolean isAutoStartup() {
    return true;
  }

  @Override
  public void stop(Runnable runnable) {
    this.stop();
    runnable.run();
  }

  @Override
  public int getPhase() {
    return 2147483647;
  }
}
