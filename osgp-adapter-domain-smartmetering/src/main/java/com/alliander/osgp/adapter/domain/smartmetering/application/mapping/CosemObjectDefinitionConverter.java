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

public class CosemObjectDefinitionConverter
extends
BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.CosemObjectDefinition, CosemObjectDefinition> {

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
    public CosemObjectDefinition convertTo(
            final com.alliander.osgp.dto.valueobjects.smartmetering.CosemObjectDefinition source,
            final Type<CosemObjectDefinition> destinationType) {
        if (source == null) {
            return null;
        }

        final CosemObisCode logicalName = this.mapper.map(source.getLogicalName(), CosemObisCode.class);
        return new CosemObjectDefinition(source.getClassId(), logicalName, source.getAttributeIndex(),
                source.getDataIndex());
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.CosemObjectDefinition convertFrom(
            final CosemObjectDefinition source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.CosemObjectDefinition> destinationType) {
        if (source == null) {
            return null;
        }

        final com.alliander.osgp.dto.valueobjects.smartmetering.CosemObisCode logicalName = this.mapper.map(
                source.getLogicalName(), com.alliander.osgp.dto.valueobjects.smartmetering.CosemObisCode.class);
        return new com.alliander.osgp.dto.valueobjects.smartmetering.CosemObjectDefinition(source.getClassId(),
                logicalName, source.getAttributeIndex(), source.getDataIndex());
    }
}
