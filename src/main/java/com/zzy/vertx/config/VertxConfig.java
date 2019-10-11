package com.zzy.vertx.config;

import io.vertx.core.VertxOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vert")
public class VertxConfig {

  private int port = 8000;

  private int workPoolSize = VertxOptions.DEFAULT_WORKER_POOL_SIZE;

  private int instance = VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE;

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
