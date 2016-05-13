/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters.CustomValueToDtoConverter;
import com.alliander.osgp.adapter.domain.smartmetering.application.services.DomainHelperService;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGasRequestData;
import com.alliander.osgp.dto.valueobjects.smartmetering.ChannelDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsGasRequestDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;

@Component
public class PeriodicReadsRequestGasDataConverter implements
        CustomValueToDtoConverter<PeriodicMeterReadsGasRequestData, PeriodicMeterReadsGasRequestDto> {

    @Autowired
    private DomainHelperService domainHelperService;

    @Override
    public PeriodicMeterReadsGasRequestDto convert(final PeriodicMeterReadsGasRequestData value,
            final SmartMeter smartMeter) throws FunctionalException {

        final SmartMeter gasMeter = this.domainHelperService.findSmartMeter(value.getDeviceIdentification());

        if (gasMeter.getChannel() != null && gasMeter.getGatewayDevice() != null
                && gasMeter.getGatewayDevice().getDeviceIdentification() != null
                && gasMeter.getGatewayDevice().getDeviceIdentification().equals(smartMeter.getDeviceIdentification())) {

            return new PeriodicMeterReadsGasRequestDto(PeriodTypeDto.valueOf(value.getPeriodType().name()),
                    value.getBeginDate(), value.getEndDate(), ChannelDto.fromNumber(gasMeter.getChannel()));
        }
        /*
         * For now, throw a FunctionalException. As soon as we can communicate
         * with some types of gas meters directly, and not through an M-Bus port
         * of an energy meter, this will have to be changed.
         */
        throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.DOMAIN_SMART_METERING,
                new AssertionError("Meter for gas reads should have a channel configured."));
    }
}
