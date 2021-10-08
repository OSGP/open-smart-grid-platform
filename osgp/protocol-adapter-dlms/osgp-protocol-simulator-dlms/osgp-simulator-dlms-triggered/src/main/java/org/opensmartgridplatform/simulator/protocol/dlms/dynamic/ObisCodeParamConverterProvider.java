/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.dynamic;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import org.openmuc.jdlms.ObisCode;

@Provider
public class ObisCodeParamConverterProvider implements ParamConverterProvider {

  @Override
  public <T> ParamConverter<T> getConverter(
      final Class<T> rawType, final Type genericType, final Annotation[] annotations) {

    if (!ObisCode.class.equals(rawType)) {
      return null;
    }

    return new ParamConverter<T>() {

      @Override
      public T fromString(final String value) {
        try {
          return rawType.cast(new ObisCode(value));
        } catch (final IllegalArgumentException e) {
          throw new BadRequestException("ObisCode param is not properly formatted", e);
        }
      }

      @Override
      public String toString(final T value) {
        return ((ObisCode) value).asDecimalString();
      }
    };
  }
}
