package com.zzy.vertx.core.message;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import org.springframework.http.MediaType;

import java.io.IOException;

public class JsonMessageConvert extends AbstractMessageConvert {

  public JsonMessageConvert() {

  }

  @Override
  public boolean canWrite(Class var1, MediaType var2) {
    return var2 != null && var2.includes(MediaType.APPLICATION_JSON) || var2.includes(MediaType.APPLICATION_JSON_UTF8);
  }

  @Override
  public Buffer write(Object var1, MediaType var2) throws IOException {
    return Json.encodeToBuffer(var1);
  }

  @Override
  public boolean canRead(Class var1, MediaType var2) {
    return var2 != null && var2.includes(MediaType.APPLICATION_JSON) || var2.includes(MediaType.APPLICATION_JSON_UTF8);
  }

  @Override
  public Object read(Buffer var1, Class type, MediaType var2) throws IOException {
    return Json.decodeValue(var1.toString(), type);
  }


}
