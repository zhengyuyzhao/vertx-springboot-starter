package com.zzy.vertx.core.router;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({RouterIniter.class})
public class VertxRouterAutoConfigure {
}
