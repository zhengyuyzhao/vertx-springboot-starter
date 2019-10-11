package com.zzy.vertx.core.router;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({VertxRouter.class, RouterIniter.class, VertxRouterAwareProcessor.class, })
public class VertxRouterAutoConfigure {
}
