/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping.informationelements;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.openmuc.j60870.ie.IeShortFloat;
import org.opensmartgridplatform.dto.da.measurements.elements.FloatMeasurementElementDto;

public class IeShortFloatConverter
    extends CustomConverter<IeShortFloat, FloatMeasurementElementDto> {

  @Override
  public FloatMeasurementElementDto convert(
      final IeShortFloat source,
      final Type<? extends FloatMeasurementElementDto> destinationType,
      final MappingContext mappingContext) {
    return new FloatMeasurementElementDto(source.getValue());
  }
}
