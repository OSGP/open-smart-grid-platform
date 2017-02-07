/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataRequestVo;
import com.alliander.osgp.dto.valueobjects.smartmetering.ObisCodeValuesDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileGenericDataRequestDto;

public class ProfileGenericDataRequestConverter extends
CustomConverter<ProfileGenericDataRequestVo, ProfileGenericDataRequestDto> {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    @Override
    public ProfileGenericDataRequestDto convert(ProfileGenericDataRequestVo source,
            Type<? extends ProfileGenericDataRequestDto> destinationType) {
        final ObisCodeValuesDto obisCodeDto = this.mapperFactory.getMapperFacade().map(source.getObisCode(),
                ObisCodeValuesDto.class);
        return new ProfileGenericDataRequestDto(obisCodeDto, source.getBeginDate(), source.getEndDate());
    }

}
