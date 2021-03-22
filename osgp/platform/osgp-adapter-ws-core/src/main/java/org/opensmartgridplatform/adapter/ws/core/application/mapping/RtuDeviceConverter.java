/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import java.util.Objects;

import org.opensmartgridplatform.domain.core.entities.RtuDevice;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class RtuDeviceConverter extends
        BidirectionalConverter<RtuDevice, org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device> {

    private final DeviceConverterHelper<RtuDevice> helper = new DeviceConverterHelper<>(RtuDevice.class);

    @Override
    public void setMapperFacade(final MapperFacade mapper) {
        super.setMapperFacade(mapper);
        this.helper.setMapperFacade(mapper);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * ma.glasnost.orika.converter.BidirectionalConverter#convertTo(java.lang
     * .Object, ma.glasnost.orika.metadata.Type,
     * ma.glasnost.orika.MappingContext)
     */
    @Override
    public org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device convertTo(final RtuDevice source,
            final Type<org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device> destinationType,
            final MappingContext mappingContext) {
        return this.helper.initJaxb(source);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * ma.glasnost.orika.converter.BidirectionalConverter#convertFrom(java.lang
     * .Object, ma.glasnost.orika.metadata.Type,
     * ma.glasnost.orika.MappingContext)
     */
    @Override
    public RtuDevice convertFrom(final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device source,
            final Type<RtuDevice> destinationType, final MappingContext mappingContext) {
        return this.helper.initEntity(source);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(this.helper);
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }
}
