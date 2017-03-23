/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.smartsocietyservices.osgp.adapter.domain.da.application.mapping;

import com.smartsocietyservices.osgp.domain.da.valueobjects.SetDataRequest;
import com.smartsocietyservices.osgp.domain.da.valueobjects.SetDataSystemIdentifier;
import com.smartsocietyservices.osgp.dto.da.SetDataRequestDto;
import com.smartsocietyservices.osgp.dto.da.SetDataSystemIdentifierDto;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.util.List;

public class SetDataRequestConverter extends BidirectionalConverter<SetDataRequest, SetDataRequestDto>
{

    @Override
    public SetDataRequestDto convertTo( final SetDataRequest source, final Type<SetDataRequestDto> destinationType )
    {
        final List<SetDataSystemIdentifierDto> setDataSystemIdentifiers = this.mapperFacade
                .mapAsList( source.getSetDataSystemIdentifiers(), SetDataSystemIdentifierDto.class );

        return new SetDataRequestDto( setDataSystemIdentifiers );
    }

    @Override
    public SetDataRequest convertFrom( final SetDataRequestDto source, final Type<SetDataRequest> destinationType )
    {
        final List<SetDataSystemIdentifier> setDataSystemIdentifiers = this.mapperFacade
                .mapAsList( source.getSetDataSystemIdentifiers(), SetDataSystemIdentifier.class );

        return new SetDataRequest( setDataSystemIdentifiers );
    }
}
