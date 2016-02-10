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

import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemDateTime;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.WindowElement;

public class WindowElementConverter extends
BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.WindowElement, WindowElement> {

    private final ConfigurationMapper mapper;

    public WindowElementConverter() {
        this.mapper = new ConfigurationMapper();
    }

    public WindowElementConverter(final ConfigurationMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof WindowElementConverter)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        final WindowElementConverter o = (WindowElementConverter) other;
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
    public WindowElement convertTo(final com.alliander.osgp.dto.valueobjects.smartmetering.WindowElement source,
            final Type<WindowElement> destinationType) {
        if (source == null) {
            return null;
        }

        return new WindowElement(this.mapper.map(source.getStartTime(), CosemDateTime.class), this.mapper.map(
                source.getEndTime(), CosemDateTime.class));
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.WindowElement convertFrom(final WindowElement source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.WindowElement> destinationType) {
        if (source == null) {
            return null;
        }

        return new com.alliander.osgp.dto.valueobjects.smartmetering.WindowElement(this.mapper.map(
                source.getStartTime(), com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTime.class),
                this.mapper.map(source.getEndTime(),
                        com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTime.class));
    }
}
