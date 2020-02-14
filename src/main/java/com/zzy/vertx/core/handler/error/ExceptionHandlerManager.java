package com.zzy.vertx.core.handler.error;

import io.vertx.ext.web.RoutingContext;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ExceptionHandlerManager {
  private final Map<Class<? extends Throwable>, ErrorHandler> exceptionHandlerMap = new ConcurrentHashMap<>();

  public ExceptionHandlerManager(){
    this.exceptionHandlerMap.put(Throwable.class, new DefaultErrorHandler());
  }

  public void addHandler(Class<? extends Throwable> c, ErrorHandler eh){
    this.exceptionHandlerMap.put(c, eh);
  }

  public void addHandlers(Map<Class<? extends Throwable>, ErrorHandler > map){
    this.exceptionHandlerMap.putAll(map);
  }

  public void handle(RoutingContext ctx, Throwable e){
    Objects.requireNonNull(e);
    ErrorHandler handler = exceptionHandlerMap.get(e.getClass());
    handler = handler == null ? exceptionHandlerMap.get(Throwable.class) : handler;
    handler.handle(ctx, e);
  }
}
