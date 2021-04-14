/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping.informationelements;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.openmuc.j60870.ie.IeQuality;
import org.opensmartgridplatform.dto.da.measurements.elements.BitmaskMeasurementElementDto;

public class IeQualityConverter extends CustomConverter<IeQuality, BitmaskMeasurementElementDto> {

  private static final int BIT_OVERFLOW = 0b00000001;
  private static final int BIT_BLOCKED = 0b00010000;
  private static final int BIT_SUBSTITUTED = 0b00100000;
  private static final int BIT_NOT_TOPICAL = 0b01000000;
  private static final int BIT_INVALID = 0b10000000;

  @Override
  public BitmaskMeasurementElementDto convert(
      final IeQuality source,
      final Type<? extends BitmaskMeasurementElementDto> destinationType,
      final MappingContext mappingContext) {
    int value = 0;
    value += source.isOverflow() ? BIT_OVERFLOW : 0;
    value += source.isBlocked() ? BIT_BLOCKED : 0;
    value += source.isSubstituted() ? BIT_SUBSTITUTED : 0;
    value += source.isNotTopical() ? BIT_NOT_TOPICAL : 0;
    value += source.isInvalid() ? BIT_INVALID : 0;
    return new BitmaskMeasurementElementDto((byte) value);
  }
}
