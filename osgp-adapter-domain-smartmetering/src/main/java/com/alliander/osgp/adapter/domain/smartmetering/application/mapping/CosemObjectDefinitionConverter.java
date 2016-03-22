/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import java.util.Objects;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemObisCode;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemObjectDefinition;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemObisCodeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemObjectDefinitionDto;

public class CosemObjectDefinitionConverter extends
        BidirectionalConverter<CosemObjectDefinitionDto, CosemObjectDefinition> {

    private final ConfigurationMapper mapper;

    public CosemObjectDefinitionConverter() {
        this.mapper = new ConfigurationMapper();
    }

    public CosemObjectDefinitionConverter(final ConfigurationMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof CosemObjectDefinitionConverter)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        final CosemObjectDefinitionConverter o = (CosemObjectDefinitionConverter) other;
        if (this.mapper == null) {
            return o.mapper == null;
        }
        return this.mapper.getClass().equals(o.mapper.getClass());
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hashCode(this.mapper);
    }

    @Override
    public CosemObjectDefinition convertTo(final CosemObjectDefinitionDto source,
            final Type<CosemObjectDefinition> destinationType) {
        if (source == null) {
            return null;
        }

        final CosemObisCode logicalName = this.mapper.map(source.getLogicalName(), CosemObisCode.class);
        return new CosemObjectDefinition(source.getClassId(), logicalName, source.getAttributeIndex(),
                source.getDataIndex());
    }

    @Override
    public CosemObjectDefinitionDto convertFrom(final CosemObjectDefinition source,
            final Type<CosemObjectDefinitionDto> destinationType) {
        if (source == null) {
            return null;
        }

        final CosemObisCodeDto logicalName = this.mapper.map(source.getLogicalName(), CosemObisCodeDto.class);
        return new CosemObjectDefinitionDto(source.getClassId(), logicalName, source.getAttributeIndex(),
                source.getDataIndex());
    }
}
