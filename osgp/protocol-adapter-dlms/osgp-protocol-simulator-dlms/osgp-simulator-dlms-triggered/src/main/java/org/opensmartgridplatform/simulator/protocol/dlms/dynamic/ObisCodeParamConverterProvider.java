// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.dynamic;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
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
