package com.zzy.vertx.core.handler.param;

import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Parameter;

public class ParamNameTransferHandler extends AbstractAnnotationTransferHandler {
  @Override
  public SpringParamDefine build(SpringParamDefine last, Parameter parameter, String paramName) {
    SpringParamDefine define = last == null ? new SpringParamDefine() : last;
    Class type = parameter.getType();
    if (last.getName() == null
      && !RoutingContext.class.equals(type)) {
      define.setName(paramName);
      define.setType(parameter.getType());
      define.setRequired(false);
      define.getHandlers().add(this);
    }
    return define;
  }

  @Override
  public Object transfer(RoutingContext context, Object lastTransfered, SpringParamDefine expression) {
    String par = context.request().getParam(expression.getName());
    Class type = expression.getType();
    Object realPar = convert(type, par);
    return realPar;
  }


}
