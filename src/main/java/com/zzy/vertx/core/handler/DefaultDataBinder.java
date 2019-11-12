package com.zzy.vertx.core.handler;

import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.autoconfigure.web.format.WebConversionService;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDataBinder {
  private Map<String, SimpleTypeConverter> converters = new ConcurrentHashMap();
  private final String dateFormat = "yyyy-MM-dd HH:mm:ss";

  DefaultDataBinder() {
    ConversionService conversionService = new WebConversionService(dateFormat);
    SimpleTypeConverter typeConverter = new SimpleTypeConverter();
    typeConverter.setConversionService(conversionService);
    converters.put(dateFormat, typeConverter);
  }

  public <T> T convertIfNecessary(@Nullable Object var1, @Nullable Class<T> var2) throws TypeMismatchException {
    return converters.get(dateFormat).convertIfNecessary(var1, var2);
  }

  public <T> T convertIfNecessary(@Nullable Object var1, @Nullable Class<T> var2, String format) throws TypeMismatchException {
    if (StringUtils.isEmpty(format)) {
      return convertIfNecessary(var1, var2);
    }
    SimpleTypeConverter converter = converters.get(format);
    if (converter == null) {
      ConversionService conversionService = new WebConversionService(format);
      SimpleTypeConverter typeConverter = new SimpleTypeConverter();
      typeConverter.setConversionService(conversionService);
      converter = typeConverter;
      converters.put(format, typeConverter);
    }
    return converter.convertIfNecessary(var1, var2);
  }

}
