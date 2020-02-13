package com.zzy.vertx.core.handler.param;

import io.vertx.ext.web.RoutingContext;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.ValidationException;
import java.lang.reflect.Parameter;
import java.util.Date;

public class PathVariableTransferHandler extends AbstractAnnotationTransferHandler {
  @Override
  public SpringParamDefine build(SpringParamDefine last, Parameter parameter, String paramName) {
    SpringParamDefine define = last == null ? new SpringParamDefine() : last;
    PathVariable paramInfo = getParam(parameter, PathVariable.class);
    if (paramInfo != null) {
      String realName = getTransferedParamName(paramInfo.name(), paramInfo.value(), paramName);
      define.setName(realName);
      define.setDefaultValue(null);
      define.setType(parameter.getType());
      define.setRequired(paramInfo.required());
      define.getHandlers().add(this);
    }
    return define;
  }

  @Override
  public Object transfer(RoutingContext context, Object lastTransfered, SpringParamDefine expression) {
    String par = context.request().getParam(expression.getName());
    Class type = expression.getType();
    if(Date.class.equals(type)){
      type = String.class;
    }
    Object realPar = convert(type, par);
    if (realPar != null) {
      return realPar;
    } else if (expression.isRequired()) {
      throw new ValidationException(expression.getName() + " is required");
    } else {
      return (convert(type, expression.getDefaultValue()));
    }
  }


}
