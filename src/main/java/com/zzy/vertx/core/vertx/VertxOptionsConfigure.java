package com.zzy.vertx.core.vertx;

import com.zzy.vertx.config.VertxConfig;
import io.vertx.core.VertxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(VertxConfig.class)
public class VertxOptionsConfigure {

  @Autowired
  private VertxConfig vertxConfig;

  @Bean
  public VertxOptions vertxOptions(){
    return vertxConfig.getVertxOptions().setEventBusOptions(vertxConfig.getEventBusOptions())
      .setEventLoopPoolSize(vertxConfig.getInstance())
      .setWorkerPoolSize(vertxConfig.getWorkPoolSize());
  }
}
