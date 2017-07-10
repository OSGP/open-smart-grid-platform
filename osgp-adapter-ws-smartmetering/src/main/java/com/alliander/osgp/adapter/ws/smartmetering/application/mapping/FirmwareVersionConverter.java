/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.FirmwareModuleType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.FirmwareVersion;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class FirmwareVersionConverter extends
        BidirectionalConverter<FirmwareVersion, com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.FirmwareVersion> {

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.FirmwareVersion convertTo(
            final FirmwareVersion source,
            final Type<com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.FirmwareVersion> destinationType,
            final MappingContext mappingContext) {

        if (source == null) {
            return null;
        }

        final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.FirmwareVersion firmwareVersion = new com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.FirmwareVersion();
        firmwareVersion.setFirmwareModuleType(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.FirmwareModuleType
                        .valueOf(source.getType().getDescription()));
        firmwareVersion.setVersion(source.getVersion());

        return firmwareVersion;
    }

    @Override
    public FirmwareVersion convertFrom(
            final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.FirmwareVersion source,
            final Type<FirmwareVersion> destinationType, final MappingContext mappingContext) {

        if (source == null) {
            return null;
        }

        final FirmwareModuleType type = FirmwareModuleType.forDescription(source.getFirmwareModuleType().name());
        final String version = source.getVersion();

        return new FirmwareVersion(type, version);
    }
}
