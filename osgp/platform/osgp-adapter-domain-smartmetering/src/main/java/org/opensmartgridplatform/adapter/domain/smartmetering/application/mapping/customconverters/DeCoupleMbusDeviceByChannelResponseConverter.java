/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DeCoupleMbusDeviceByChannelResponse;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DeCoupleMbusDeviceResponseDto;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

@Component
public class DeCoupleMbusDeviceByChannelResponseConverter
        extends CustomConverter<DeCoupleMbusDeviceResponseDto, DeCoupleMbusDeviceByChannelResponse> {

    @Override
    public DeCoupleMbusDeviceByChannelResponse convert(final DeCoupleMbusDeviceResponseDto source,
            final Type<? extends DeCoupleMbusDeviceByChannelResponse> type, final MappingContext mappingContext) {

        if (source == null) {
            return null;
        }

        return new DeCoupleMbusDeviceByChannelResponse(source.getMbusDeviceIdentification(),
                source.getChannelElementValues().getChannel());
    }
}
