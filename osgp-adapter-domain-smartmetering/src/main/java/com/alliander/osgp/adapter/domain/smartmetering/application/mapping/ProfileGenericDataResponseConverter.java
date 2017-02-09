/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import java.util.List;
import java.util.stream.Collectors;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.CaptureObjectItemVo;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ObisCodeValues;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryItemVo;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataResponseVo;
import com.alliander.osgp.dto.valueobjects.smartmetering.CaptureObjectItemDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileEntryItemDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileGenericDataResponseDto;

public class ProfileGenericDataResponseConverter extends
CustomConverter<ProfileGenericDataResponseDto, ProfileGenericDataResponseVo> {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    @Override
    public ProfileGenericDataResponseVo convert(ProfileGenericDataResponseDto source,
            Type<? extends ProfileGenericDataResponseVo> destinationType) {

        final ObisCodeValues obisCode = this.mapperFactory.getMapperFacade().map(source.getLogicalName(),
                ObisCodeValues.class);

        List<CaptureObjectItemVo> captureObjectItemDtoList = source.getCaptureObject().stream()
                .map(dto -> this.map(dto)).collect(Collectors.toList());
        List<ProfileEntryItemVo> profileEntryItemDtoList = source.getProfileEntries().stream()
                .map(dto -> this.map(dto)).collect(Collectors.toList());

        return new ProfileGenericDataResponseVo(obisCode, captureObjectItemDtoList, profileEntryItemDtoList);
    }

    private CaptureObjectItemVo map(CaptureObjectItemDto dto) {
        return this.mapperFactory.getMapperFacade().map(dto, CaptureObjectItemVo.class);
    }

    private ProfileEntryItemVo map(ProfileEntryItemDto dto) {
        return this.mapperFactory.getMapperFacade().map(dto, ProfileEntryItemVo.class);
    }

}
