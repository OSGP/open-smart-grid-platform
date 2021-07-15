/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;

public class ObisCodeValuesConverter
    extends BidirectionalConverter<
        ObisCodeValues,
        org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues> {

  @Override
  public org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues convertTo(
      final ObisCodeValues source,
      final Type<org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues>
          destinationType,
      final MappingContext context) {

    return new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues(
        (byte) source.getA(),
        (byte) source.getB(),
        (byte) source.getC(),
        (byte) source.getD(),
        (byte) source.getE(),
        (byte) source.getF());
  }

  @Override
  public ObisCodeValues convertFrom(
      final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues source,
      final Type<ObisCodeValues> destinationType,
      final MappingContext context) {

    final ObisCodeValues result = new ObisCodeValues();
    result.setA((short) (source.getA() & 0xFF));
    result.setB((short) (source.getB() & 0xFF));
    result.setC((short) (source.getC() & 0xFF));
    result.setD((short) (source.getD() & 0xFF));
    result.setE((short) (source.getE() & 0xFF));
    result.setF((short) (source.getF() & 0xFF));
    return result;
  }
}
