/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgpfoundation.osgp.adapter.domain.da.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.osgpfoundation.osgp.domain.da.valueobjects.GetDeviceModelRequest;
import org.osgpfoundation.osgp.dto.da.GetDeviceModelRequestDto;

public class GetDeviceModelRequestConverter extends BidirectionalConverter<GetDeviceModelRequest, GetDeviceModelRequestDto> {

    @Override
    public GetDeviceModelRequestDto convertTo(final GetDeviceModelRequest source, final Type<GetDeviceModelRequestDto> destinationType) {
        return new GetDeviceModelRequestDto("");
    }

    @Override
    public GetDeviceModelRequest convertFrom(final GetDeviceModelRequestDto source, final Type<GetDeviceModelRequest> destinationType) {
        return new GetDeviceModelRequest();
    }

}
