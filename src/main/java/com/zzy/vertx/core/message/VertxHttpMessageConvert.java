package com.zzy.vertx.core.message;

import io.vertx.core.buffer.Buffer;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.List;

public interface VertxHttpMessageConvert<T> {

  void setSupportedMediaTypes(List<MediaType> list);

  boolean canWrite(Class<?> var1, @Nullable MediaType var2);

  List<MediaType> getSupportedMediaTypes();

  Buffer write(T var1, @Nullable MediaType var2) throws IOException;

  boolean canRead(Class<?> var1, @Nullable MediaType var2);

  T read(Buffer var1, Class<T> type, @Nullable MediaType var2) throws IOException;
}
