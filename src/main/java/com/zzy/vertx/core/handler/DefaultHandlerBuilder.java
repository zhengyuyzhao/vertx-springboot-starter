package com.zzy.vertx.core.handler;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.zzy.vertx.core.handler.param.ParamTransferManager;
import com.zzy.vertx.core.handler.param.SpringParamDefine;
import com.zzy.vertx.core.message.MessageConvertManager;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;

import javax.validation.ValidationException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class DefaultHandlerBuilder implements VertxHandlerBuilder {
  private static final Logger logger = LoggerFactory.getLogger(DefaultHandlerBuilder.class);
  public static final String DEFAULT_PRODUCT = "application/json;charset=UTF-8";

  @Autowired
  private VertxHandlerInterceptorManager interceptorManager;

  @Autowired
  private MessageConvertManager convertManager;

  @Autowired
  private ParamTransferManager paramTransferManager;

  @Override
  public Handler<RoutingContext> build(Method method, Object bean, boolean isAsync) {
    RequestMapping mappingInfo = getMappingForMethod(method);
    List<SpringParamDefine> springParamDefines;
    Handler<RoutingContext> handler = null;
    if (mappingInfo != null) {
      springParamDefines = paramTransferManager.build(method);
      String[] products = mappingInfo.produces();
      String product = products.length > 0 ? StringUtils.arrayToDelimitedString(products, ";") : DEFAULT_PRODUCT;

      Method realMethod ;
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
        boolean flag = interceptorManager.preHandle(ctx, handlerMethod);
        if (flag) {
          List<Object> paramList = paramTransferManager.transfer(ctx, springParamDefines);
          Object result;
          if (paramList == null) {
            result = access.invoke(bean, index);
          }else {
            result = access.invoke(bean, index, paramList.toArray());
          }

          interceptorManager.postHandle(ctx, handlerMethod, result);
          if (!(ctx.response().ended() || ctx.response().closed())) {
            ctx.response().putHeader("Content-Type", product);
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
        if (e instanceof TypeMismatchException || e instanceof ValidationException) {
          ctx.fail(400, e);
        } else {
          ctx.fail(500, e);
        }
      }
    };
  }



  protected Handler<RoutingContext> buildAsyncHandler(List<SpringParamDefine> springParamDefines, Method method, Object bean, String product) {
    MethodAccess access = MethodAccess.get(bean.getClass());
    int index = access.getIndex(method.getName(), method.getParameterTypes());
    return ctx -> {
      try {
        HandlerMethod handlerMethod = new HandlerMethod(bean, method);
        boolean flag = interceptorManager.preHandle(ctx, handlerMethod);
        if (flag) {
          List<Object> paramList = paramTransferManager.transfer(ctx, springParamDefines);
          if (paramList == null) {
            access.invoke(bean, index);
          }else {
            access.invoke(bean, index, paramList.toArray());
          }
        } else {
          ctx.response().close();
        }
      } catch (Exception e) {
//        e.printStackTrace();
        logger.error(e.getMessage());
        if (e instanceof TypeMismatchException || e instanceof ValidationException) {
          ctx.fail(400, e);
        } else {
          ctx.fail(500, e);
        }
      }
    };
  }

  private RequestMapping getMappingForMethod(Method method) {
    RequestMapping info = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
    return info;
  }

}
