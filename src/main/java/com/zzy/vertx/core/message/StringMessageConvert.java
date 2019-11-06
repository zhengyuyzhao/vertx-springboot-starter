package com.zzy.vertx.core.message;

import io.vertx.core.buffer.Buffer;
import org.springframework.http.MediaType;

import java.io.IOException;

public class StringMessageConvert extends AbstractMessageConvert {

  public StringMessageConvert() {

  }

  @Override
  public boolean canWrite(Class var1, MediaType var2) {
    return var1 == String.class;
  }

  @Override
  public boolean canRead(Class var1, MediaType var2) {
    return var1 == String.class;
  }

  @Override
  public Object read(Buffer var1, Class type, MediaType var2) throws IOException {
    return var1.toString();
  }

  @Override
  public Buffer write(Object var1, MediaType var2) throws IOException {
    return Buffer.buffer(var1.toString().getBytes(var2.getCharset() != null ? var2.getCharset() : DEFAULT_CHARSET));
  }


}
