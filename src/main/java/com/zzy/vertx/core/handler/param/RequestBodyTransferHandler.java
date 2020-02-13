package com.zzy.vertx.core.handler.param;

import com.zzy.vertx.core.message.MessageConvertManager;
import io.vertx.ext.web.RoutingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.ValidationException;
import java.lang.reflect.Parameter;

public class RequestBodyTransferHandler extends AbstractAnnotationTransferHandler {
  @Autowired
  private MessageConvertManager convertManager;

  @Override
  public SpringParamDefine build(SpringParamDefine last, Parameter parameter, String paramName) {
    SpringParamDefine define = last == null ? new SpringParamDefine() : last;
    RequestBody paramInfo = getParam(parameter, RequestBody.class);
    if (paramInfo != null) {
      define.setName("body");
      define.setDefaultValue(null);
      define.setType(parameter.getType());
      define.setRequired(paramInfo.required());
      define.getHandlers().add(this);
    }
    return define;
  }

  @Override
  public Object transfer(RoutingContext ctx, Object lastTransfered, SpringParamDefine expression) throws Exception{
    if (ctx.getBody() == null || ctx.getBody().length() <= 0) {
      if (expression.isRequired()) {
        throw new ValidationException("requestBody is required");
      }
      return null;
    } else {
      String consume = ctx.request().getHeader("Content-Type");
      MediaType mediaType = StringUtils.isEmpty(consume) ? MediaType.ALL : MediaType.valueOf(consume);
      Object realBody = convertManager.decode(expression.getType(), ctx.getBody(), mediaType);
      if (realBody != null) {
        return realBody;
      }  else {
        return null;
      }
    }
  }


}
