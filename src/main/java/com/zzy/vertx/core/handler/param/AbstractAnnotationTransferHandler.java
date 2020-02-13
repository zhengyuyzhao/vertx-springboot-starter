package com.zzy.vertx.core.handler.param;

import com.zzy.vertx.core.handler.DefaultDataBinder;
import io.vertx.core.json.Json;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

public abstract class AbstractAnnotationTransferHandler implements ParamTransferHandler {
  private static final DefaultDataBinder dataBinder = new DefaultDataBinder();

  protected <A extends Annotation> A getParam(Parameter parameter, Class<A> handlerType) {
    A info = AnnotatedElementUtils.findMergedAnnotation(parameter, handlerType);
    return info;
  }

  protected String getTransferedParamName(String name, String value, String parameterName) {
    String result = value;
    if (StringUtils.isEmpty(result)) {
      result = name;
    }
    if (StringUtils.isEmpty(result)) {
      result = parameterName;
    }
    return result;
  }

  protected  <T> T convert(Class<T> clz, String o) {
    if (o == null || "\n\t\t\n\t\t\n\ue000\ue001\ue002\n\t\t\t\t\n".equals(o)) {
      return null;
    }
    if (BeanUtils.isSimpleProperty(clz)) {
      return dataBinder.convertIfNecessary(o, clz);
    }
    try {
      return Json.decodeValue(o, clz);
    } catch (Exception e) {
      return null;
    }
  }

  public static DefaultDataBinder getDataBinder() {
    return dataBinder;
  }
}
