package com.zzy.vertx.core.message;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import org.springframework.http.MediaType;

import java.io.IOException;

public class XmlMessageConvert extends AbstractMessageConvert {
  private  XmlMapper xmlMapper;
  public XmlMessageConvert() {
    xmlMapper = new XmlMapper();
  }
  public XmlMessageConvert(XmlMapper xmlMapper) {
    this.xmlMapper = xmlMapper;
  }

  @Override
  public boolean canWrite(Class var1, MediaType var2) {
    return var2 != null && var2.includes(MediaType.APPLICATION_XML) || var2.includes(MediaType.APPLICATION_XHTML_XML);
  }

  @Override
  public Buffer write(Object var1, MediaType var2) throws IOException {
    return Buffer.buffer(xmlMapper.writeValueAsBytes(var1));
  }

  @Override
  public boolean canRead(Class var1, MediaType var2) {
    return var2 != null && var2.includes(MediaType.APPLICATION_XML) || var2.includes(MediaType.APPLICATION_XHTML_XML);
  }

  @Override
  public Object read(Buffer var1, Class type, MediaType var2) throws IOException {
    return xmlMapper.readerFor(type).readValue(var1.toString());
  }

}
