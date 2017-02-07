/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.ObisCodeValues;
import com.alliander.osgp.dto.valueobjects.smartmetering.ObisCodeValuesDto;

public class ObisCodeValuesConverter extends BidirectionalConverter<ObisCodeValues, ObisCodeValuesDto> {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    @Override
    public ObisCodeValuesDto convertTo(ObisCodeValues source, Type<ObisCodeValuesDto> destinationType) {
        return this.mapperFactory.getMapperFacade().map(source, ObisCodeValuesDto.class);
    }

    @Override
    public ObisCodeValues convertFrom(ObisCodeValuesDto source, Type<ObisCodeValues> destinationType) {
        return this.mapperFactory.getMapperFacade().map(source, ObisCodeValues.class);
    }

}
