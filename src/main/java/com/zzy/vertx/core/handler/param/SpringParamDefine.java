package com.zzy.vertx.core.handler.param;

import java.util.ArrayList;
import java.util.List;

public class SpringParamDefine {
  private String name;
//  private String value;
  private String defaultValue;
  private String dateFormat;
  private Class type;
  private boolean required;

  private final List<ParamTransferHandler> handlers = new ArrayList<>();

  public SpringParamDefine(){

  }

  public SpringParamDefine(String name, String defaultValue, Class type, boolean required) {
    this.name = name;
//    this.value = value;
    this.defaultValue = defaultValue;
    this.type = type;
    this.required = required;
  }

  public SpringParamDefine(String name, String defaultValue, Class type, boolean required, String dateFormat) {
    this.name = name;
//    this.value = value;
    this.defaultValue = defaultValue;
    this.type = type;
    this.required = required;
    this.dateFormat = dateFormat;
  }

  public List<ParamTransferHandler> getHandlers() {
    return handlers;
  }

  public String getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Class getType() {
    return type;
  }

  public void setType(Class type) {
    this.type = type;
  }
}
