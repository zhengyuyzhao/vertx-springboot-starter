package com.zzy.vertx.core.message;

import io.vertx.core.buffer.Buffer;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageConvertManager {
  private final List<VertxHttpMessageConvert> converts = new ArrayList<>();

  public Buffer encode(Class type, Object out, MediaType mediaType) throws IOException {
    for (VertxHttpMessageConvert convert : converts) {
      if (convert.canWrite(type, mediaType)) {
        return convert.write(out, mediaType);
      }
    }
    return Buffer.buffer(String.valueOf(out).getBytes());
  }
  public Object decode(Class type, Buffer in, MediaType mediaType) throws IOException{
    for (VertxHttpMessageConvert convert : converts) {
      if (convert.canRead(type, mediaType)) {
        return convert.read(in, type, mediaType);
      }
    }
    return in.toJson();
  }

  public void addMessageConverts(VertxHttpMessageConvert... convert) {
    this.converts.addAll(Arrays.asList(convert));
  }
}
