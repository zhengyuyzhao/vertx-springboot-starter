package com.zzy.vertx.core.handler.param;

import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Parameter;

public interface ParamTransferHandler {
  default SpringParamDefine build(SpringParamDefine last, Parameter parameter, String realName){
    return last == null ? new SpringParamDefine() : last;
  }
  Object transfer(RoutingContext context, Object lastTransfered, SpringParamDefine param)  throws Exception;
}
