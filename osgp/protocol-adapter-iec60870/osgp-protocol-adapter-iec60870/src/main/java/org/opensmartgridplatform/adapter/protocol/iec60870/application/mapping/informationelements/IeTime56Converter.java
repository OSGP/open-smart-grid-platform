/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping.informationelements;

import java.util.Objects;
import java.util.TimeZone;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.openmuc.j60870.ie.IeTime56;
import org.opensmartgridplatform.dto.da.measurements.elements.TimestampMeasurementElementDto;

public class IeTime56Converter extends CustomConverter<IeTime56, TimestampMeasurementElementDto> {

  private final TimeZone timeZone;

  public IeTime56Converter(final TimeZone timeZone) {
    this.timeZone = timeZone;
  }

  @Override
  public TimestampMeasurementElementDto convert(
      final IeTime56 source,
      final Type<? extends TimestampMeasurementElementDto> destinationType,
      final MappingContext mappingContext) {

    return new TimestampMeasurementElementDto(source.getTimestamp(1970, this.timeZone));
  }

  @Override
  public boolean equals(final Object that) {
    if (this == that) {
      return true;
    }

    if (!(that instanceof IeTime56Converter)) {
      return false;
    }

    return Objects.equals(this.timeZone, ((IeTime56Converter) that).timeZone);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.timeZone);
  }
}
