/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters;

import java.util.Objects;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemObisCode;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemObisCodeDto;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public class CosemObisCodeDtoConverter extends CustomConverter<CosemObisCode, CosemObisCodeDto> {

    private final ConfigurationMapper configurationMapper;

    public CosemObisCodeDtoConverter() {
        this.configurationMapper = new ConfigurationMapper();
    }

    public CosemObisCodeDtoConverter(final ConfigurationMapper configurationMapper) {
        this.configurationMapper = configurationMapper;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof CosemObisCodeDtoConverter)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        final CosemObisCodeDtoConverter o = (CosemObisCodeDtoConverter) other;
        if (this.configurationMapper == null) {
            return o.configurationMapper == null;
        }
        return this.configurationMapper.getClass().equals(o.configurationMapper.getClass());
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hashCode(this.configurationMapper);
    }

    @Override
    public CosemObisCodeDto convert(final CosemObisCode source, final Type<? extends CosemObisCodeDto> destinationType,
            final MappingContext mappingContext) {

        if (source == null) {
            return null;
        }

        return new CosemObisCodeDto(source.toByteArray());
    }
}
