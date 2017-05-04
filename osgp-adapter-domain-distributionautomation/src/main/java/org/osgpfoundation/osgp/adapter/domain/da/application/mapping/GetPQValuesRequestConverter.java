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
import org.osgpfoundation.osgp.domain.da.valueobjects.GetPQValuesRequest;
import org.osgpfoundation.osgp.dto.da.GetPQValuesRequestDto;

public class GetPQValuesRequestConverter extends BidirectionalConverter<GetPQValuesRequest, GetPQValuesRequestDto> {

    @Override
    public GetPQValuesRequestDto convertTo(final GetPQValuesRequest source, final Type<GetPQValuesRequestDto> destinationType) {
        return new GetPQValuesRequestDto("");
    }

    @Override
    public GetPQValuesRequest convertFrom(final GetPQValuesRequestDto source, final Type<GetPQValuesRequest> destinationType) {
        return new GetPQValuesRequest();
    }
}
