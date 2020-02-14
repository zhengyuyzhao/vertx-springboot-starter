package com.zzy.vertx.core.handler.param;

import io.vertx.core.http.Cookie;
import io.vertx.ext.web.RoutingContext;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.validation.ValidationException;
import java.lang.reflect.Parameter;
import java.util.Date;

public class CookieValueTransferHandler extends AbstractAnnotationTransferHandler {
  @Override
  public SpringParamDefine build(SpringParamDefine last, Parameter parameter, String paramName) {
    SpringParamDefine define = last == null ? new SpringParamDefine() : last;
    CookieValue header = getParam(parameter, CookieValue.class);
    if (header != null) {
      String realName = getTransferedParamName(header.name(), header.value(), paramName);
      define.setName(realName);
      define.setDefaultValue(header.defaultValue());
      define.setType(parameter.getType());
      define.setRequired(header.required());
      define.getHandlers().add(this);
    }
    return define;
  }

  @Override
  public Object transfer(RoutingContext context, Object lastTransfered, SpringParamDefine expression) {
    Cookie cookie =  context.request().getCookie(expression.getName());
    if(cookie == null){
      return null;
    }
    String par = cookie.getValue();
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
