package com.zzy.vertx.core.vertx;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({VertxOptionsConfigure.class, VertxConfigure.class})
public class VertxAutoConfigure {
}
