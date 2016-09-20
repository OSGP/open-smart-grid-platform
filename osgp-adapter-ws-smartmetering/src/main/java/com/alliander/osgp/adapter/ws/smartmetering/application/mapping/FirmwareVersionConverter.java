/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.FirmwareVersion;

public class FirmwareVersionConverter
        extends
CustomConverter<FirmwareVersion, com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.FirmwareVersion> {

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.FirmwareVersion convert(
            FirmwareVersion source,
            Type<? extends com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.FirmwareVersion> destinationType) {

        if (source != null) {

            final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.FirmwareVersion firmwareVersion = new com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.FirmwareVersion();
            firmwareVersion.setFirmwareModuleType(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.FirmwareModuleType
                    .valueOf(source.getType().name()));
            firmwareVersion.setVersion(source.getVersion());

            return firmwareVersion;
        }

        return null;
    }
}
