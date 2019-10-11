package com.zzy.starter;

import com.zzy.vertx.core.handler.VertxHandlerInterceptor;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;


@Component
public class Interceptor implements VertxHandlerInterceptor {
  @Override
  public boolean preHandle(RoutingContext context, Object handler) throws Exception {
    HandlerMethod handlerMethod = (HandlerMethod) handler;
    if(handlerMethod.getMethod().isAnnotationPresent(ResponseBody.class)){
      context.response().end("not support responsebody");
      return false;
    }
    return true;
  }
}
