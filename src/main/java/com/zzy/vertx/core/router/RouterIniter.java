package com.zzy.vertx.core.router;

import com.zzy.vertx.core.annotaion.AsyncHandler;
import com.zzy.vertx.core.handler.VertxHandlerInterceptorManager;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RouterIniter implements BeanPostProcessor , ApplicationContextAware {
  private static final Logger logger = LoggerFactory.getLogger(RouterIniter.class);
  public static final String DEFAULT_PRODUCT = "application/json;charset=UTF-8";
  private Router router;
  private ApplicationContext applicationContext;
  @Autowired
  private VertxHandlerInterceptorManager interceptorManager;

  public RouterIniter(Router router) {
    this.router = router;
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    Class cla = bean.getClass();
    String baseUrl = "";
    if (cla.isAnnotationPresent(Controller.class) || cla.isAnnotationPresent(RestController.class)){
      if (cla.isAnnotationPresent(RequestMapping.class)) {
        baseUrl = ((RequestMapping) cla.getAnnotation(RequestMapping.class)).value()[0];
        baseUrl = StringUtils.isEmpty(baseUrl) ? "" : baseUrl;
      }
      Method[] methods = cla.getDeclaredMethods();
      for (Method method : methods) {
        RequestMapping mappingInfo = getMappingForMethod(method);
        List<VertxParam> vertxParams;
        if (mappingInfo != null) {
          Parameter[] parameters = method.getParameters();
          vertxParams = getSpringParams(parameters, method);
          RequestMethod[] methodSet = mappingInfo.method();
          String[] products = mappingInfo.produces();
          String product = products.length > 0 ? products[0] : DEFAULT_PRODUCT;
          int pathLength = mappingInfo.path().length;
          String path = baseUrl + (pathLength > 0 ? mappingInfo.path()[0] : "");
          path = formatSpringPath(path);
          logger.info("---------path---{}", path);
          if(!checkPath(path)){
            continue;
          }
          Handler<RoutingContext> handler;
          boolean async = false;
          if (method.isAnnotationPresent(AsyncHandler.class)) {
            async = true;
            handler = buildAsyncHandler(vertxParams, method, bean, product);
          } else {
            handler = buildHandler(vertxParams, method, bean, product);
          }

          try {
            Route route = null;
            if (methodSet.length > 0) {
              for (RequestMethod requestMethod : methodSet) {
                route = router.route(convertHttpMethod(requestMethod), path);
              }
            } else {
              route = router.route(path);
            }
            if (async) {
              route.handler(handler);
            } else {
              route.blockingHandler(handler);
            }
          } catch (Exception e) {
            e.printStackTrace();
            logger.error("------router error----{}", e.getMessage());
          }
        }
      }
    }
    return bean;
  }

  private boolean checkPath(String path){
    if(StringUtils.isEmpty(path) || !path.startsWith("/") || path.contains("$")){
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

  private String[] getParameterNames(Method method) {
    LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
    return u.getParameterNames(method);
  }

  private List<VertxParam> getSpringParams(Parameter[] parameters, Method method) {
    List<VertxParam> vertxParams = new ArrayList<>();
    String[] realNames = getParameterNames(method);
    int i = 0;
    for (Parameter parameter : parameters) {
      String paremeterName = realNames[i++];
      RequestParam paramInfo = getParam(parameter, RequestParam.class);
      PathVariable pathVariable = getParam(parameter, PathVariable.class);
      RequestBody requestBody = getParam(parameter, RequestBody.class);
      if (paramInfo != null) {
        String realName = getRealParamName(paramInfo.name(), paramInfo.value(), paremeterName);
        vertxParams.add(new VertxParam(realName, realName, paramInfo.defaultValue(), parameter.getType(), paramInfo.required()));
        continue;
      }
      if (pathVariable != null) {
        String realName = getRealParamName(pathVariable.name(), pathVariable.value(), paremeterName);
        vertxParams.add(new VertxParam(realName, realName, null, parameter.getType(), true));
        continue;
      }
      if (requestBody != null) {
        vertxParams.add(new VertxParam("body", "body", null, parameter.getType(), requestBody.required()));
        continue;
      }
      vertxParams.add(new VertxParam(paremeterName, paremeterName, null, parameter.getType(), false));
    }
    return vertxParams;
  }

  private String getRealParamName(String name, String value, String parameterName) {
    String result = value;
    if (StringUtils.isEmpty(result)) {
      result = name;
    }
    if (StringUtils.isEmpty(result)) {
      result = parameterName;
    }
    return result;
  }


  private Handler<RoutingContext> buildHandler(List<VertxParam> vertxParams, Method method, Object bean, String product) {
    return ctx -> {
      try {
        HandlerMethod handlerMethod = new HandlerMethod(bean, method);
        boolean flag = interceptorManager.preHandle(ctx, handlerMethod);
        if (flag) {
          List<Object> paramList = buildHandlerInvokeParamList(vertxParams, ctx);
          if (paramList == null) {
            return;
          }
          Object result = method.invoke(bean, paramList.toArray());
          interceptorManager.postHandle(ctx, handlerMethod, result);
          if (!(ctx.response().ended() || ctx.response().closed())) {
            ctx.response().putHeader("Content-Type", product);
            ctx.response().end(Json.encode(result));
          }
        } else {
          ctx.response().close();
        }
      } catch (Exception e) {
        e.printStackTrace();
        ctx.response().setStatusCode(500).end(e.getMessage());
      }
    };
  }

  private Handler<RoutingContext> buildAsyncHandler(List<VertxParam> vertxParams, Method method, Object bean, String product) {
    return ctx -> {
      try {
        HandlerMethod handlerMethod = new HandlerMethod(bean, method);
        boolean flag = interceptorManager.preHandle(ctx, handlerMethod);
        if (flag) {
          List<Object> paramList = buildHandlerInvokeParamList(vertxParams, ctx);
          if (paramList == null) {
            return;
          }
          method.invoke(bean, paramList.toArray());
        } else {
          ctx.response().close();
        }
      } catch (Exception e) {
        e.printStackTrace();
        ctx.response().setStatusCode(500).end(e.getMessage());
      }
    };
  }

  private List<Object> buildHandlerInvokeParamList(List<VertxParam> vertxParams, RoutingContext ctx) {
    List<Object> paramList = new ArrayList<>();
    for (VertxParam expression : vertxParams) {
      if (RoutingContext.class.equals(expression.getType())) {
        paramList.add(ctx);
      } else if ("body".equals(expression.getValue())) {
        Object realBody = Json.decodeValue(ctx.getBody(), expression.getType());
        if (realBody != null) {
          paramList.add(realBody);
        } else if (expression.isRequired()) {
          ctx.response().setStatusCode(400).end("requestBody is required");
          return null;
        } else {
          paramList.add(null);
        }
      } else {
        Object par = ctx.request().getParam(expression.getValue());
        Object realPar = convert(expression.getType(), par);
        if (realPar != null) {
          paramList.add(realPar);
        } else if (expression.isRequired()) {
          ctx.response().setStatusCode(400).end(expression.getValue() + " is required");
          return null;
        } else {
          paramList.add(convert(expression.getType(), expression.getDefaultValue()));
        }
      }
    }
    return paramList;
  }

  private <T> T convert(Class<T> clz, Object o) {
    if (o == null || "\n\t\t\n\t\t\n\ue000\ue001\ue002\n\t\t\t\t\n".equals(o)) {
      return null;
    }
    try {
      return Json.decodeValue(Json.encodeToBuffer(o), clz);
    } catch (Exception e) {
      return null;
    }
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

  protected <A extends Annotation> A getParam(Parameter parameter, Class<A> handlerType) {
    A info = AnnotatedElementUtils.findMergedAnnotation(parameter, handlerType);
    return info;
  }


  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
