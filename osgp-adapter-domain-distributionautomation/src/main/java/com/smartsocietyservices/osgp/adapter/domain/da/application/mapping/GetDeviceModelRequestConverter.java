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

import com.smartsocietyservices.osgp.domain.da.valueobjects.GetDeviceModelRequest;
import com.smartsocietyservices.osgp.dto.da.GetDeviceModelRequestDto;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class GetDeviceModelRequestConverter extends BidirectionalConverter<GetDeviceModelRequest, GetDeviceModelRequestDto> {

    @Override
    public GetDeviceModelRequestDto convertTo( final GetDeviceModelRequest source, final Type<GetDeviceModelRequestDto> destinationType ) {
        return new GetDeviceModelRequestDto( source.getDeviceIdentifier() );
    }

    @Override
    public GetDeviceModelRequest convertFrom( final GetDeviceModelRequestDto source, final Type<GetDeviceModelRequest> dingen ) {
        return new GetDeviceModelRequest( source.getDeviceIdentifier() );
    }
}
