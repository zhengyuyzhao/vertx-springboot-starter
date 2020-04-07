package com.zzy.vertx.core.router;

import com.zzy.vertx.core.annotaion.AsyncHandler;
import com.zzy.vertx.core.handler.VertxHandlerBuilder;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class RouterIniter implements SmartLifecycle, ApplicationContextAware {
  private static final Logger logger = LoggerFactory.getLogger(RouterIniter.class);
  public static final String DEFAULT_PRODUCT = MediaType.APPLICATION_JSON_UTF8_VALUE;
  public static final String DEFAULT_CONSUME = "*";
  private boolean running;
  @Autowired
  private Router router;
  private ApplicationContext applicationContext;

  @Autowired
  private VertxHandlerBuilder handlerBuilder;

  private void buildRouter(Object bean, Class cla) {
    String baseUrl = "";
    if (cla.isAnnotationPresent(Controller.class) || cla.isAnnotationPresent(RestController.class)) {
      if (cla.isAnnotationPresent(RequestMapping.class)) {
        baseUrl = ((RequestMapping) cla.getAnnotation(RequestMapping.class)).value()[0];
        baseUrl = StringUtils.isEmpty(baseUrl) ? "" : baseUrl;
      }
      Method[] methods = cla.getDeclaredMethods();
      for (Method method : methods) {
        RequestMapping mappingInfo = getMappingForMethod(method);
        if (mappingInfo != null) {
          RequestMethod[] methodSet = mappingInfo.method();
          int pathLength = mappingInfo.path().length;
          String path = baseUrl + (pathLength > 0 ? mappingInfo.path()[0] : "");
          path = formatSpringPath(path);
          logger.info("---------path---{}", path);
          if (!checkPath(path)) {
            continue;
          }
          String[] products = mappingInfo.produces();
          String[] consumes = mappingInfo.consumes();
          String consume = consumes.length > 0 ? StringUtils.arrayToDelimitedString(consumes, ";") : DEFAULT_CONSUME;
          String product = products.length > 0 ? StringUtils.arrayToDelimitedString(products, ";") : DEFAULT_PRODUCT;
          Handler<RoutingContext> handler;
          boolean async = false;
          if (method.isAnnotationPresent(AsyncHandler.class)) {
            async = true;
          }

          handler = handlerBuilder.build(method, bean, async);
          try {
            List<Route> routes = new ArrayList<>();
            if (methodSet.length > 0) {
              for (RequestMethod requestMethod : methodSet) {
                routes.add(router.route(convertHttpMethod(requestMethod), path));
              }
            } else {
              routes.add(router.route(path));
            }
            for(Route route : routes){
              route.produces(product).consumes(consume);
              if (async) {
                route.handler(handler);
              } else {
                route.blockingHandler(handler, false);
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
            logger.error("------router error----{}", e.getMessage());
          }
        }
      }
    }
  }

  private boolean checkPath(String path) {
    if (StringUtils.isEmpty(path) || !path.startsWith("/") || path.contains("$")) {
      return false;
    }
    return true;
  }

  private String formatSpringPath(String path) {
    if (StringUtils.isEmpty(path)) {
      return "/";
    }
    if (!path.startsWith("/")) {
      path = "/" + path;
    }
    path = path.replaceAll("\\{", ":");
    path = path.replaceAll("}", "");

    return path;
  }

  private HttpMethod convertHttpMethod(RequestMethod method) {
    switch (method) {
      case GET:
        return HttpMethod.GET;
      case PUT:
        return HttpMethod.PUT;
      case HEAD:
        return HttpMethod.HEAD;
      case POST:
        return HttpMethod.POST;
      case PATCH:
        return HttpMethod.PATCH;
      case TRACE:
        return HttpMethod.TRACE;
      case DELETE:
        return HttpMethod.DELETE;
      case OPTIONS:
        return HttpMethod.OPTIONS;
      default:
        return null;
    }
  }

  protected RequestMapping getMappingForMethod(Method method) {
    RequestMapping info = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
    return info;
  }


  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public void start() {
    logger.info("RouterIniter SmartLifecycle");
    running = true;
    Map<String, Object> controllers = new HashMap<>();
    controllers.putAll(this.applicationContext.getBeansWithAnnotation(Controller.class));
    controllers.putAll(this.applicationContext.getBeansWithAnnotation(RestController.class));
    for (Object bean : controllers.values()) {
      Class cla;
      if (AopUtils.isAopProxy(bean)) {
        cla = AopUtils.getTargetClass(bean);
      } else {
        cla = bean.getClass();
      }
      buildRouter(bean, cla);
    }
    stop();
  }

  @Override
  public void stop() {
    running = false;
  }

  @Override
  public boolean isRunning() {
    return running;
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
