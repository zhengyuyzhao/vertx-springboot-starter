package com.zzy.vertx.core.vertx;

import com.hazelcast.config.Config;
import com.zzy.vertx.core.router.RouterIniter;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.spi.cluster.hazelcast.ConfigUtil;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Configuration
public class VertxConfigure {
  private static final Logger logger = LoggerFactory.getLogger(RouterIniter.class);
  @Autowired
  private VertxOptions options;

  @Bean
  @Order(10)
  @ConditionalOnMissingBean(value = Vertx.class)
  @ConditionalOnClass(name = {"com.hazelcast.core.HazelcastInstance", "io.vertx.spi.cluster.hazelcast.HazelcastClusterManager"})
  public Vertx vertxHazelcast() throws ExecutionException, InterruptedException {
    logger.info("-----------------vert--init--vertxHazelcast");
    Config hazelcastConfig = ConfigUtil.loadConfig();
    HazelcastClusterManager manager = new HazelcastClusterManager(hazelcastConfig);
    options.setClusterManager(manager);
    CompletableFuture<Vertx> future = new CompletableFuture<>();
    Vertx.clusteredVertx(options, ar -> {
      if (ar.succeeded()) {
        future.complete(ar.result());
      } else {
        future.completeExceptionally(ar.cause());
      }
    });
    Vertx vertx = future.get();
    return vertx;
  }

  @Bean
  @Order(20)
  @ConditionalOnMissingBean(value = Vertx.class)
  @ConditionalOnClass(name = {"org.apache.ignite.Ignite", "io.vertx.spi.cluster.ignite.IgniteClusterManager"})
  public Vertx vertxIgnite() throws ExecutionException, InterruptedException {
    logger.info("-----------------vert--init--vertxIgnite");
    IgniteClusterManager manager = new IgniteClusterManager();
    options.setClusterManager(manager);
    CompletableFuture<Vertx> future = new CompletableFuture<>();
    Vertx.clusteredVertx(options, ar -> {
      if (ar.succeeded()) {
        future.complete(ar.result());
      } else {
        future.completeExceptionally(ar.cause());
      }
    });
    Vertx vertx = future.get();
    return vertx;
  }

  @Bean
  @Order(30)
  @ConditionalOnMissingBean(value = Vertx.class)
  @ConditionalOnClass(name = {"org.apache.zookeeper.ZooKeeper", "io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager"})
  public Vertx vertxZookeeper() throws ExecutionException, InterruptedException {
    logger.info("-----------------vert--init--vertxZookeeper");
    ZookeeperClusterManager manager = new ZookeeperClusterManager();
    options.setClusterManager(manager);
    CompletableFuture<Vertx> future = new CompletableFuture<>();
    Vertx.clusteredVertx(options, ar -> {
      if (ar.succeeded()) {
        future.complete(ar.result());
      } else {
        future.completeExceptionally(ar.cause());
      }
    });
    Vertx vertx = future.get();
    return vertx;
  }

  @Bean
  @Order(Ordered.LOWEST_PRECEDENCE)
  @ConditionalOnMissingBean(value = Vertx.class)
  @ConditionalOnClass(Vertx.class)
  public Vertx vertxSingle() {
    logger.info("----------------vert--init--single");
    return Vertx.vertx(options);
  }
}
