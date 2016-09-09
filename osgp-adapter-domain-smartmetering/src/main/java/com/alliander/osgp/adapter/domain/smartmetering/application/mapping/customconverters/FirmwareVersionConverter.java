/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.dto.valueobjects.FirmwareVersionDto;

public class FirmwareVersionConverter extends
        CustomConverter<FirmwareVersionDto, com.alliander.osgp.domain.core.valueobjects.smartmetering.FirmwareVersion> {

    @Override
    public com.alliander.osgp.domain.core.valueobjects.smartmetering.FirmwareVersion convert(FirmwareVersionDto source,
            Type<? extends com.alliander.osgp.domain.core.valueobjects.smartmetering.FirmwareVersion> destinationType) {

        if (source != null) {

            return new com.alliander.osgp.domain.core.valueobjects.smartmetering.FirmwareVersion(
                    com.alliander.osgp.domain.core.valueobjects.smartmetering.FirmwareModuleType.valueOf(source
                            .getFirmwareModuleType().name()), source.getVersion());
        }

        return null;
    }
}
