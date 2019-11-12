package com.zzy.vertx.core.handler;

import com.zzy.vertx.core.message.MessageConvertManager;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultHandlerBuilder implements VertxHandlerBuilder{
  private static final Logger logger = LoggerFactory.getLogger(DefaultHandlerBuilder.class);
  public static final String DEFAULT_PRODUCT = "application/json;charset=UTF-8";
  @Autowired
  private VertxHandlerInterceptorManager interceptorManager;

  @Autowired
  private MessageConvertManager convertManager;
  @Override
  public Handler<RoutingContext> build(Method method, Object bean, boolean isAsync) {
    RequestMapping mappingInfo = getMappingForMethod(method);
    List<VertxParam> vertxParams;
    Handler<RoutingContext> handler = null;
    if (mappingInfo != null) {
      Parameter[] parameters = method.getParameters();
      vertxParams = getSpringParams(parameters, method);
      String[] products = mappingInfo.produces();
      String product = products.length > 0 ? StringUtils.arrayToDelimitedString(products, ";") : DEFAULT_PRODUCT;
      if (isAsync) {
        handler = buildAsyncHandler(vertxParams, method, bean, product);
      } else {
        handler = buildHandler(vertxParams, method, bean, product);
      }
    }
    return handler;
  }

  protected Handler<RoutingContext> buildHandler(List<VertxParam> vertxParams, Method method, Object bean, String product) {
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
            ctx.response().end(convertManager.encode(method.getReturnType(), result, MediaType.valueOf(product)));
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

  private List<Object> buildHandlerInvokeParamList(List<VertxParam> vertxParams, RoutingContext ctx) throws Exception{
    List<Object> paramList = new ArrayList<>();
    for (VertxParam expression : vertxParams) {
      if (RoutingContext.class.equals(expression.getType())) {
        paramList.add(ctx);
      } else if ("body".equals(expression.getValue())) {
        if(ctx.getBody() == null || ctx.getBody().length() <= 0){
          paramList.add(null);
        }else{
          String consume = ctx.request().getHeader("Content-Type");
          MediaType mediaType = StringUtils.isEmpty(consume) ? MediaType.ALL : MediaType.valueOf(consume);
          Object realBody = convertManager.decode(expression.getType(), ctx.getBody(), mediaType);
          if (realBody != null) {
            paramList.add(realBody);
          } else if (expression.isRequired()) {
            ctx.response().setStatusCode(400).end("requestBody is required");
            return null;
          } else {
            paramList.add(null);
          }
        }

      } else {
        String par = ctx.request().getParam(expression.getValue());
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

  private <T> T convert(Class<T> clz, String o) {
    if (o == null || "\n\t\t\n\t\t\n\ue000\ue001\ue002\n\t\t\t\t\n".equals(o)) {
      return null;
    }
    if(clz.isAssignableFrom(String.class) || clz.isPrimitive()){
      return clz.cast(o);
    }
    try {
      return Json.decodeValue(o, clz);
    } catch (Exception e) {
      return null;
    }
  }

  protected Handler<RoutingContext> buildAsyncHandler(List<VertxParam> vertxParams, Method method, Object bean, String product) {
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
  private String[] getParameterNames(Method method) {
    LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
    return u.getParameterNames(method);
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


  protected List<VertxParam> getSpringParams(Parameter[] parameters, Method method) {
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

  private RequestMapping getMappingForMethod(Method method) {
    RequestMapping info = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
    return info;
  }

  private  <A extends Annotation> A getParam(Parameter parameter, Class<A> handlerType) {
    A info = AnnotatedElementUtils.findMergedAnnotation(parameter, handlerType);
    return info;
  }
}
