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

import com.smartsocietyservices.osgp.domain.da.valueobjects.GetPQValuesRequest;
import com.smartsocietyservices.osgp.dto.da.GetPQValuesRequestDto;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class GetPQValuesRequestConverter extends BidirectionalConverter<GetPQValuesRequest, GetPQValuesRequestDto> {

    @Override
    public GetPQValuesRequestDto convertTo( final GetPQValuesRequest source, final Type<GetPQValuesRequestDto> destinationType ) {
        return new GetPQValuesRequestDto( source.getDeviceIdentifier() );
    }

    @Override
    public GetPQValuesRequest convertFrom( final GetPQValuesRequestDto source, final Type<GetPQValuesRequest> destinationType ) {
        return new GetPQValuesRequest( source.getDeviceIdentifier() );
    }

}
