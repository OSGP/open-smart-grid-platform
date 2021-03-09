/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.repositories.SmartMeterRepository;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DeCoupleMbusDeviceByChannelResponse;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DeCoupleMbusDeviceResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeCoupleMbusDeviceByChannelResponseConverter
        extends CustomConverter<DeCoupleMbusDeviceResponseDto, DeCoupleMbusDeviceByChannelResponse> {

    @Autowired
    private final SmartMeterRepository smartMeteringDeviceRepository;

    public DeCoupleMbusDeviceByChannelResponseConverter(final SmartMeterRepository smartMeteringDeviceRepository) {
        this.smartMeteringDeviceRepository = smartMeteringDeviceRepository;
    }

    @Override
    public DeCoupleMbusDeviceByChannelResponse convert(DeCoupleMbusDeviceResponseDto source,
            Type<? extends DeCoupleMbusDeviceByChannelResponse> type, MappingContext mappingContext) {

        if (source == null) {
            return null;
        }

        final SmartMeter mbusDevice = this.smartMeteringDeviceRepository.findByMBusIdentificationNumber(
                Long.valueOf(source.getChannelElementValues().getIdentificationNumber()),
                source.getChannelElementValues().getManufacturerIdentification());

        final String mbusDeviceIdentification = mbusDevice!=null?mbusDevice.getDeviceIdentification():null;

        return new DeCoupleMbusDeviceByChannelResponse(mbusDeviceIdentification,
                        source.getChannelElementValues().getChannel());
    }
}
