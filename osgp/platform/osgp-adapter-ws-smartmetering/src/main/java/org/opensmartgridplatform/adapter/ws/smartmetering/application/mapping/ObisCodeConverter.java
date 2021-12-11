/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemObisCode;

public class ObisCodeConverter extends BidirectionalConverter<CosemObisCode, ObisCodeValues> {

  @Override
  public ObisCodeValues convertTo(
      final CosemObisCode source,
      final Type<ObisCodeValues> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    final ObisCodeValues convertedObisCode = new ObisCodeValues();
    convertedObisCode.setA((short) source.getA());
    convertedObisCode.setB((short) source.getB());
    convertedObisCode.setC((short) source.getC());
    convertedObisCode.setD((short) source.getD());
    convertedObisCode.setE((short) source.getE());
    convertedObisCode.setF((short) source.getF());

    return convertedObisCode;
  }

  @Override
  public CosemObisCode convertFrom(
      final ObisCodeValues source,
      final Type<CosemObisCode> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    return new CosemObisCode(
        source.getA(), source.getB(), source.getC(), source.getD(), source.getE(), source.getF());
  }
}
