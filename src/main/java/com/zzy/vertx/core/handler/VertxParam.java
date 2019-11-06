package com.zzy.vertx.core.handler;

public class VertxParam {
  private String name;
  private String value;
  private String defaultValue;
  private Class type;
  private boolean required;

  public VertxParam(String name, String value, String defaultValue, Class type, boolean required) {
    this.name = name;
    this.value = value;
    this.defaultValue = defaultValue;
    this.type = type;
    this.required = required;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
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
