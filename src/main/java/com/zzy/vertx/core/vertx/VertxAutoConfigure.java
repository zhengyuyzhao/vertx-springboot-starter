package com.zzy.vertx.core.vertx;

import io.vertx.core.Vertx;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PreDestroy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Configuration
@Import({VertxOptionsConfigure.class, VertxConfigure.class})
public class VertxAutoConfigure implements DisposableBean {
  @Autowired
  private Vertx vertx;

  @PreDestroy
  void close() throws ExecutionException, InterruptedException {
    CompletableFuture<Void> future = new CompletableFuture<>();
    vertx.close(ar -> future.complete(null));
    future.get();
  }

  @Override
  public void destroy() throws Exception {
    CompletableFuture<Void> future = new CompletableFuture<>();
    vertx.close(ar -> future.complete(null));
    future.get();
  }
}
