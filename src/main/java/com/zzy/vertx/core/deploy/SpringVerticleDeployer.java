package com.zzy.vertx.core.deploy;

import com.zzy.vertx.config.VertxConfig;
import com.zzy.vertx.core.VertxServer;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.spi.VerticleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Configuration
@ConditionalOnBean(SpringVerticleFactory.class)
public class SpringVerticleDeployer implements SmartLifecycle {
  private static final Logger logger = LoggerFactory.getLogger(SpringVerticleDeployer.class);
  private boolean running;
  @Autowired
  private VerticleFactory verticleFactory;

  @Autowired
  private VertxConfig vertxConfig;

  @Autowired
  private Vertx vertx;

  public void deploy() throws ExecutionException, InterruptedException {
    vertx.registerVerticleFactory(verticleFactory);
    // Scale the verticles on cores: create 4 instances during the deployment
    DeploymentOptions options = new DeploymentOptions()
      .setHa(vertxConfig.isHa())
      .setWorker(false)
      .setWorkerPoolSize(vertxConfig.getWorkPoolSize())
      .setInstances(vertxConfig.getInstance());
    CompletableFuture<Void> future = new CompletableFuture<>();
    vertx.deployVerticle(verticleFactory.prefix() + ":" + VertxServer.class.getName(), options, stringAsyncResult -> {
      if (stringAsyncResult.succeeded()) {
        logger.info("----------vertx success start at port : {}", vertxConfig.getPort());
        future.complete(null);
      } else {
        future.completeExceptionally(stringAsyncResult.cause());
        stringAsyncResult.cause().fillInStackTrace().printStackTrace();
        System.exit(1);
      }
    });

    future.get();
  }

  @Override
  public void start() {
    this.running = false;
    try {
      deploy();
      this.running = true;
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Override
  public void stop() {
    CompletableFuture<Void> future = new CompletableFuture<>();
    vertx.close(ar -> future.complete(null));
    try {
      future.get();
    } catch (Exception e) {
      e.printStackTrace();
    }
    this.running = false;
  }

  @Override
  public boolean isRunning() {
    return this.running;
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
