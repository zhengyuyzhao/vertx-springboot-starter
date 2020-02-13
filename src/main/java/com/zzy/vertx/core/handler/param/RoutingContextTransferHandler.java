package com.zzy.vertx.core.handler.param;

import com.zzy.vertx.core.message.MessageConvertManager;
import io.vertx.ext.web.RoutingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.ValidationException;
import java.lang.reflect.Parameter;

public class RoutingContextTransferHandler extends AbstractAnnotationTransferHandler {
  @Autowired
  private MessageConvertManager convertManager;

  @Override
  public SpringParamDefine build(SpringParamDefine last, Parameter parameter, String paramName) {
    SpringParamDefine define = last == null ? new SpringParamDefine() : last;
    if(RoutingContext.class.equals(parameter.getType())){
      define.setType(RoutingContext.class);
      define.getHandlers().add(this);
    }
    return define;
  }

  @Override
  public Object transfer(RoutingContext ctx, Object lastTransfered, SpringParamDefine expression) throws Exception{
    return ctx;
  }


}
