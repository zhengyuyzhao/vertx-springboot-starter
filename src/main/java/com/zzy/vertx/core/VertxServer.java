package com.zzy.vertx.core;

import com.zzy.vertx.config.VertxConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Component
@ConditionalOnBean(Router.class)
@EnableConfigurationProperties(VertxConfig.class)
@Scope(SCOPE_PROTOTYPE)
public class VertxServer extends AbstractVerticle {

  @Autowired
  private VertxConfig vertxConfig;

  @Autowired
  private Router router;


  @Override
  public void start(Future<Void> startFuture) throws Exception {
    super.start();
    vertx.createHttpServer().requestHandler(router).listen(vertxConfig.getPort(), httpServerAsyncResult -> {
      if(httpServerAsyncResult.succeeded()){
        startFuture.complete();
      }else {
        startFuture.fail(httpServerAsyncResult.cause());
      }
    });

  }

  @Override
  public void stop(Future<Void> endFuture) throws Exception {
    super.stop();
    vertx.deployVerticle(this);
    endFuture.complete();
  }

}
