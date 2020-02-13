package com.zzy.vertx.core.handler.param;

import io.vertx.ext.web.RoutingContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class ParamTransferManager {
  private final List<ParamTransferHandler> handlers = new ArrayList<>();

  public ParamTransferManager() {
  }

  public void addHandler(ParamTransferHandler handler) {
    this.handlers.add(handler);
  }

  public List<SpringParamDefine> build(Method method) {
    String[] realNames = getParameterNames(method);
    int i = 0;
    List<SpringParamDefine> springParamDefines = new ArrayList<>();
    Parameter[] parameters = method.getParameters();
    for (Parameter parameter : parameters) {
      String paramRealName = realNames[i];
      i++;
      SpringParamDefine paramDefine = new SpringParamDefine();
      for (ParamTransferHandler handler : handlers) {
        paramDefine = handler.build(paramDefine, parameter, paramRealName);
      }
      springParamDefines.add(paramDefine);
    }
    return springParamDefines;
  }

  public List<Object> transfer(RoutingContext context, List<SpringParamDefine> params) throws Exception{
    List<Object> transfers = new ArrayList<>();
    for (SpringParamDefine springParamDefine : params) {
      Object transfered = null;
      for (ParamTransferHandler handler : springParamDefine.getHandlers()) {
        transfered = handler.transfer(context, transfered, springParamDefine);
      }
      transfers.add(transfered);
    }
    return transfers;
  }

  private String[] getParameterNames(Method method) {
    LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
    return u.getParameterNames(method);
  }
}
