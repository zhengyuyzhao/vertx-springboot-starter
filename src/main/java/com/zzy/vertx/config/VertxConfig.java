package com.zzy.vertx.config;

import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vert")
public class VertxConfig {

  private int port = 8000;

  private int workPoolSize = VertxOptions.DEFAULT_WORKER_POOL_SIZE;

  private int instance = VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE;

  private boolean routerAop = true;

  private boolean ha = true;

  private VertxOptions vertxOptions = new VertxOptions();

  private EventBusOptions eventBusOptions = new EventBusOptions();

  public boolean isHa() {
    return ha;
  }

  public void setHa(boolean ha) {
    this.ha = ha;
  }

  public VertxOptions getVertxOptions() {
    return vertxOptions;
  }

  public void setVertxOptions(VertxOptions vertxOptions) {
    this.vertxOptions = vertxOptions;
  }

  public EventBusOptions getEventBusOptions() {
    return eventBusOptions;
  }

  public void setEventBusOptions(EventBusOptions eventBusOptions) {
    this.eventBusOptions = eventBusOptions;
  }

  public boolean isRouterAop() {
    return routerAop;
  }

  public void setRouterAop(boolean routerAop) {
    this.routerAop = routerAop;
  }

  public int getInstance() {
    return instance;
  }

  public void setInstance(int instance) {
    this.instance = instance;
  }

  public int getWorkPoolSize() {
    return workPoolSize;
  }

  public void setWorkPoolSize(int workPoolSize) {
    this.workPoolSize = workPoolSize;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }
}
