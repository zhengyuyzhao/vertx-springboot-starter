package com.zzy.vertx.core.handler;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.zzy.vertx.core.handler.error.ErrorHandler;
import com.zzy.vertx.core.handler.error.ExceptionHandlerManager;
import com.zzy.vertx.core.handler.param.ParamTransferManager;
import com.zzy.vertx.core.handler.param.SpringParamDefine;
import com.zzy.vertx.core.message.MessageConvertManager;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultHandlerBuilder implements VertxHandlerBuilder {
  private static final Logger logger = LoggerFactory.getLogger(DefaultHandlerBuilder.class);
  public static final String DEFAULT_PRODUCT = "application/json;charset=UTF-8";
  public static final String PLAIN_PRODUCT = "text/plain;charset=UTF-8";

  @Autowired
  private VertxHandlerInterceptorManager interceptorManager;

  @Autowired
  private MessageConvertManager convertManager;

  @Autowired
  private ParamTransferManager paramTransferManager;

  @Autowired
  private ExceptionHandlerManager exceptionHandlerManager;

  @Override
  public Map<Class<? extends Throwable>, ErrorHandler> buildExceptionHandler(Object bean) {
    Map<Class<? extends Throwable>, ErrorHandler> errorHandlerMap = new HashMap<>();
    Class cla;
    if (AopUtils.isAopProxy(bean)) {
      cla = AopUtils.getTargetClass(bean);
    } else {
      cla = bean.getClass();
    }
    Method[] methods = cla.getDeclaredMethods();
    for (Method method : methods) {
      if (!method.isAnnotationPresent(ExceptionHandler.class)) {
        continue;
      }
      ExceptionHandler exceptionHandler = AnnotatedElementUtils.findMergedAnnotation(method, ExceptionHandler.class);
      ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(method, ResponseStatus.class);
      HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
      if (responseStatus != null) {
        httpStatus = responseStatus.value().equals(HttpStatus.INTERNAL_SERVER_ERROR) ? responseStatus.code() : responseStatus.value();
      }
      Class<? extends Throwable>[] throwables = exceptionHandler.value();
      ErrorHandler errorHandler = buildExceptionHandler(method, bean, httpStatus);
      if (throwables.length == 0) {
        errorHandlerMap.put(Throwable.class, errorHandler);
      } else {
        for (Class<? extends Throwable> throwable : throwables) {
          errorHandlerMap.put(throwable, errorHandler);
        }
      }
    }
    return errorHandlerMap;
  }

  @Override
  public Handler<RoutingContext> build(Method method, Object bean, boolean isAsync) {
    RequestMapping mappingInfo = getMappingForMethod(method);
    List<SpringParamDefine> springParamDefines;
    Handler<RoutingContext> handler = null;
    if (mappingInfo != null) {
      springParamDefines = paramTransferManager.build(method);
      String[] products = mappingInfo.produces();
      String product = products.length > 0 ? StringUtils.arrayToDelimitedString(products, ";") : DEFAULT_PRODUCT;

      Method realMethod;
      try {
        realMethod = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
      } catch (NoSuchMethodException e) {
        realMethod = method;
      }
      if (isAsync) {
        handler = buildAsyncHandler(springParamDefines, realMethod, bean, product);
      } else {
        handler = buildHandler(springParamDefines, realMethod, bean, product);
      }
    }
    return handler;
  }

  protected Handler<RoutingContext> buildHandler(List<SpringParamDefine> springParamDefines, Method method, Object bean, String product) {
    MethodAccess access = MethodAccess.get(bean.getClass());
    int index = access.getIndex(method.getName(), method.getParameterTypes());
    return ctx -> {
      try {
        HandlerMethod handlerMethod = new HandlerMethod(bean, method);
        setBaseResponseHeader(ctx, product);
        boolean flag = interceptorManager.preHandle(ctx, handlerMethod);
        if (flag) {
          List<Object> paramList = paramTransferManager.transfer(ctx, springParamDefines);
          Object result;
          if (paramList == null) {
            result = access.invoke(bean, index);
          } else {
            result = access.invoke(bean, index, paramList.toArray());
          }

          interceptorManager.postHandle(ctx, handlerMethod, result);
          if (!(ctx.response().ended() || ctx.response().closed())) {
            if (result == null) {
              ctx.response().end("null");
            } else {
              ctx.response().end(convertManager.encode(method.getReturnType(), result, MediaType.valueOf(product)));
            }
          }
        } else {
          ctx.response().close();
        }
      } catch (Exception e) {
//        e.printStackTrace();
        logger.error(e.getMessage());
        exceptionHandlerManager.handle(ctx, e);
      }
    };
  }


  protected Handler<RoutingContext> buildAsyncHandler(List<SpringParamDefine> springParamDefines, Method method, Object bean, String product) {
    MethodAccess access = MethodAccess.get(bean.getClass());
    int index = access.getIndex(method.getName(), method.getParameterTypes());
    return ctx -> {
      try {
        HandlerMethod handlerMethod = new HandlerMethod(bean, method);
        setBaseResponseHeader(ctx, product);
        boolean flag = interceptorManager.preHandle(ctx, handlerMethod);
        if (flag) {
          List<Object> paramList = paramTransferManager.transfer(ctx, springParamDefines);
          if (paramList == null) {
            access.invoke(bean, index);
          } else {
            access.invoke(bean, index, paramList.toArray());
          }
        } else {
          ctx.response().close();
        }
      } catch (Exception e) {
//        e.printStackTrace();
        logger.error(e.getMessage());
        exceptionHandlerManager.handle(ctx, e);
      }
    };
  }


  protected ErrorHandler buildExceptionHandler(Method method, Object bean, HttpStatus httpStatus) {
    MethodAccess access = MethodAccess.get(bean.getClass());
    int index = access.getIndex(method.getName(), method.getParameterTypes());
    Parameter[] parameters = method.getParameters();
    return (ctx, throwable) -> {
      List<Object> invokeParams = null;
      for (Parameter parameter : parameters) {
        if (invokeParams == null) {
          invokeParams = new ArrayList<>();
        }
        if (Throwable.class.isAssignableFrom(parameter.getType())) {
          invokeParams.add(throwable);
        } else if (parameter.getType() == RoutingContext.class) {
          invokeParams.add(ctx);
        } else {
          invokeParams.add(null);
        }
      }
      Object result;
      if (invokeParams == null) {
        result = access.invoke(bean, index);
      } else {
        result = access.invoke(bean, index, invokeParams.toArray());
      }
      if (httpStatus != null) {
        ctx.response().setStatusCode(httpStatus.value());
      }
      if (result == null) {
        ctx.response().end();
      } else if (result instanceof String) {
        ctx.response().putHeader("Content-Type", PLAIN_PRODUCT);
        ctx.response().end((String) result);
      } else {
        ctx.response().putHeader("Content-Type", DEFAULT_PRODUCT);
        ctx.response().end(Json.encodePrettily(result));
      }
    };
  }

  private RequestMapping getMappingForMethod(Method method) {
    RequestMapping info = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
    return info;
  }

  private void setBaseResponseHeader(RoutingContext context, String product) {
    context.response().putHeader("Content-Type", product);
  }

}
