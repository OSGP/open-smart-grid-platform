/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.smartsocietyservices.osgp.adapter.domain.da.application.mapping;

import com.smartsocietyservices.osgp.domain.da.valueobjects.GetDataRequest;
import com.smartsocietyservices.osgp.domain.da.valueobjects.SystemFilter;
import com.smartsocietyservices.osgp.dto.da.GetDataRequestDto;
import com.smartsocietyservices.osgp.dto.da.SystemFilterDto;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.util.List;

public class GetDataRequestConverter extends BidirectionalConverter<GetDataRequest, GetDataRequestDto> {

    @Override
    public GetDataRequestDto convertTo(final GetDataRequest source, final Type<GetDataRequestDto> destinationType) {
        final List<SystemFilterDto> systemFilters = this.mapperFacade.mapAsList(source.getSystemFilters(),
                SystemFilterDto.class);

        return new GetDataRequestDto(systemFilters);
    }

    @Override
    public GetDataRequest convertFrom(final GetDataRequestDto source, final Type<GetDataRequest> destinationType) {
        final List<SystemFilter> systemFilters = this.mapperFacade.mapAsList(source.getSystemFilters(),
                SystemFilter.class);

        return new GetDataRequest(systemFilters);
    }

}
