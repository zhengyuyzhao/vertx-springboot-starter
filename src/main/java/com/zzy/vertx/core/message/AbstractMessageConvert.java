package com.zzy.vertx.core.message;

import org.springframework.http.MediaType;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public abstract class AbstractMessageConvert implements VertxHttpMessageConvert {
  public static final Charset DEFAULT_CHARSET = Charset.forName(StandardCharsets.UTF_8.name());
  private volatile List<MediaType> supportedMediaTypes = Collections.emptyList();


  @Override
  public void setSupportedMediaTypes(List list) {
    supportedMediaTypes.addAll(list);
  }

  @Override
  public boolean canWrite(Class var1, MediaType var2) {
    return false;
  }

  @Override
  public boolean canRead(Class var1, MediaType var2) {
    return false;
  }

  @Override
  public List<MediaType> getSupportedMediaTypes() {
    return supportedMediaTypes;
  }

}
