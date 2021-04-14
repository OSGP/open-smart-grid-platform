/*
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.Outage;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.OutageDto;

public class OutagesConverter extends BidirectionalConverter<OutageDto, Outage> {

  @Override
  public Outage convertTo(
      final OutageDto source, final Type<Outage> destinationType, final MappingContext context) {
    if (source == null) {
      return null;
    }

    return new Outage(source.getTimestamp(), source.getDuration());
  }

  @Override
  public OutageDto convertFrom(
      final Outage source, final Type<OutageDto> destinationType, final MappingContext context) {
    if (source == null) {
      return null;
    }

    return new OutageDto(source.getEndTime(), source.getDuration());
  }
}
