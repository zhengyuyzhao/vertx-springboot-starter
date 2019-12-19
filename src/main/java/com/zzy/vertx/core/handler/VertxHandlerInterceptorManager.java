package com.zzy.vertx.core.handler;

import io.vertx.ext.web.RoutingContext;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

public class VertxHandlerInterceptorManager {

  private List<VertxHandlerInterceptor> handlerInterceptors = new ArrayList<>();

  public VertxHandlerInterceptorManager( List<VertxHandlerInterceptor> handlerInterceptors){
    this.handlerInterceptors = handlerInterceptors;
  }
  public VertxHandlerInterceptorManager(){
  }

  public VertxHandlerInterceptorManager addInterceptor(VertxHandlerInterceptor vertxHandlerInterceptor){
    this.handlerInterceptors.add(vertxHandlerInterceptor);
    return this;
  }

  public boolean preHandle(RoutingContext context, Object handler) throws Exception{
    if(handlerInterceptors.size() == 0){
      return true;
    }
    for(VertxHandlerInterceptor interceptor : handlerInterceptors){
      boolean result = interceptor.preHandle(context, handler);
      if(!result){
        return false;
      }
      continue;
    }
    return true;
  }

  public void postHandle(RoutingContext context, Object handler, Object data) throws Exception{
    if(handlerInterceptors.size() == 0){
      return;
    }
    for(VertxHandlerInterceptor interceptor : handlerInterceptors){
      interceptor.postHandle(context, handler, data);
    }
  }
}
