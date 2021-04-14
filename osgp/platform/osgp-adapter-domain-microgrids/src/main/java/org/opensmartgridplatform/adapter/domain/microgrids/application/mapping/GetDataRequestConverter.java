/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.microgrids.application.mapping;

import java.util.List;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.microgrids.valueobjects.GetDataRequest;
import org.opensmartgridplatform.domain.microgrids.valueobjects.SystemFilter;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataRequestDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.SystemFilterDto;

public class GetDataRequestConverter
    extends BidirectionalConverter<GetDataRequest, GetDataRequestDto> {

  @Override
  public GetDataRequestDto convertTo(
      final GetDataRequest source,
      final Type<GetDataRequestDto> destinationType,
      final MappingContext context) {
    final List<SystemFilterDto> systemFilters =
        this.mapperFacade.mapAsList(source.getSystemFilters(), SystemFilterDto.class);

    return new GetDataRequestDto(systemFilters);
  }

  @Override
  public GetDataRequest convertFrom(
      final GetDataRequestDto source,
      final Type<GetDataRequest> destinationType,
      final MappingContext context) {
    final List<SystemFilter> systemFilters =
        this.mapperFacade.mapAsList(source.getSystemFilters(), SystemFilter.class);

    return new GetDataRequest(systemFilters);
  }
}
