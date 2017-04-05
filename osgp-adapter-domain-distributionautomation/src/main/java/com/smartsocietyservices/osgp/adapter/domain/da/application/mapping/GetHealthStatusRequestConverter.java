/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.smartsocietyservices.osgp.adapter.domain.da.application.mapping;

import com.smartsocietyservices.osgp.domain.da.valueobjects.GetHealthStatusRequest;
import com.smartsocietyservices.osgp.dto.da.GetHealthStatusRequestDto;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class GetHealthStatusRequestConverter extends BidirectionalConverter<GetHealthStatusRequest, GetHealthStatusRequestDto> {

    @Override
    public GetHealthStatusRequestDto convertTo( final GetHealthStatusRequest source, final Type<GetHealthStatusRequestDto> destinationType ) {
        return new GetHealthStatusRequestDto( source.getDeviceIdentifier() );
    }

    @Override
    public GetHealthStatusRequest convertFrom( final GetHealthStatusRequestDto source, final Type<GetHealthStatusRequest> dingen ) {
        return new GetHealthStatusRequest( source.getDeviceIdentifier() );
    }
}
