package com.zzy.vertx.core.handler.param;

import io.vertx.ext.web.RoutingContext;
import org.springframework.format.annotation.DateTimeFormat;

import java.lang.reflect.Parameter;

public class DateTimeFormatTransferHandler extends AbstractAnnotationTransferHandler {

  @Override
  public SpringParamDefine build(SpringParamDefine last, Parameter parameter, String paramName) {
    SpringParamDefine define = last == null ? new SpringParamDefine() : last;
    DateTimeFormat paramInfo = getParam(parameter, DateTimeFormat.class);
    if (paramInfo != null) {
      define.getHandlers().add(this);
    }
    return define;
  }

  @Override
  public Object transfer(RoutingContext context, Object lastTransfered, SpringParamDefine param) throws Exception {
    if (lastTransfered == null) {
      return null;
    }
    return getDataBinder().convertIfNecessary(lastTransfered, param.getType());
  }


}
