/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.ClockStatusBit;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemObisCode;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemObjectDefinition;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupSms;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SendDestinationAndMethod;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.TransportServiceType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.WindowElement;
import com.alliander.osgp.dto.valueobjects.smartmetering.ClockStatusBitDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ClockStatusDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTimeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemObisCodeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemObjectDefinitionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemTimeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MessageTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupSmsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SendDestinationAndMethodDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.TransportServiceTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.WindowElementDto;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public class PushSetupSmsDtoConverter
        extends CustomConverter<PushSetupSms, com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupSmsDto> {

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupSmsDto convert(final PushSetupSms source,
            final Type<? extends com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupSmsDto> destinationType,
            final MappingContext context) {

        if (source == null) {
            return null;
        }

        final PushSetupSmsDto.Builder builder = new PushSetupSmsDto.Builder();

        final List<WindowElementDto> windowElementDtos = new ArrayList<>();
        final List<WindowElement> communicationWindows = source.getCommunicationWindow();
        if (communicationWindows != null) {
            for (final WindowElement element : communicationWindows) {
                final CosemDateDto cosemDateDtoStart = new CosemDateDto(element.getStartTime().getDate().getYear(),
                        element.getStartTime().getDate().getMonth(), element.getStartTime().getDate().getDayOfMonth(),
                        element.getStartTime().getDate().getDayOfWeek());

                final CosemTimeDto cosemTimeDtoStart = new CosemTimeDto(element.getStartTime().getTime().getHour(),
                        element.getStartTime().getTime().getMinute(), element.getStartTime().getTime().getSecond(),
                        element.getStartTime().getTime().getHundredths());

                final Set<ClockStatusBitDto> clockStatusBitDtoStart = new HashSet<>();
                final Set<ClockStatusBit> statusBitsStart = element.getStartTime().getClockStatus().getStatusBits();
                if (statusBitsStart != null) {
                    for (final ClockStatusBit ClockStatusBit : statusBitsStart) {
                        clockStatusBitDtoStart.add(ClockStatusBitDto.valueOf(ClockStatusBit.name()));
                    }
                }

                final CosemDateTimeDto cosemDateTimeDtoStart = new CosemDateTimeDto(cosemDateDtoStart,
                        cosemTimeDtoStart, element.getStartTime().getDeviation(),
                        new ClockStatusDto((clockStatusBitDtoStart.isEmpty()) ? null : clockStatusBitDtoStart));

                final CosemDateDto cosemDateDtoEnd = new CosemDateDto(element.getEndTime().getDate().getYear(),
                        element.getEndTime().getDate().getMonth(), element.getEndTime().getDate().getDayOfMonth(),
                        element.getEndTime().getDate().getDayOfWeek());

                final CosemTimeDto cosemTimeDtoEnd = new CosemTimeDto(element.getEndTime().getTime().getHour(),
                        element.getEndTime().getTime().getMinute(), element.getEndTime().getTime().getSecond(),
                        element.getEndTime().getTime().getHundredths());

                final Set<ClockStatusBitDto> clockStatusBitDtoEnd = new HashSet<>();
                final Set<ClockStatusBit> statusBitsEnd = element.getEndTime().getClockStatus().getStatusBits();
                if (statusBitsEnd != null) {
                    for (final ClockStatusBit ClockStatusBit : statusBitsEnd) {
                        clockStatusBitDtoEnd.add(ClockStatusBitDto.valueOf(ClockStatusBit.name()));
                    }
                }

                final CosemDateTimeDto cosemDateTimeDtoEnd = new CosemDateTimeDto(cosemDateDtoEnd, cosemTimeDtoEnd,
                        element.getEndTime().getDeviation(),
                        new ClockStatusDto((clockStatusBitDtoEnd.isEmpty()) ? null : clockStatusBitDtoEnd));

                final WindowElementDto windowElementDto = new WindowElementDto(cosemDateTimeDtoStart,
                        cosemDateTimeDtoEnd);
                windowElementDtos.add(windowElementDto);
            }

            builder.withCommunicationWindow(windowElementDtos);
        }

        final CosemObisCode cosemObisCode = source.getLogicalName();
        if (cosemObisCode != null) {
            final CosemObisCodeDto cosemObisCodeDto = new CosemObisCodeDto(cosemObisCode.getA(), cosemObisCode.getB(),
                    cosemObisCode.getC(), cosemObisCode.getD(), cosemObisCode.getE(), cosemObisCode.getF());
            builder.withLogicalName(cosemObisCodeDto);
        }

        builder.withNumberOfRetries(source.getNumberOfRetries());

        final List<CosemObjectDefinitionDto> cosemObjectDefinitionDtos = new ArrayList<>();
        if (source.getPushObjectList() != null) {
            for (final CosemObjectDefinition cosemObjectDefinition : source.getPushObjectList()) {
                final CosemObjectDefinitionDto cosemObjectDefinitionDto = new CosemObjectDefinitionDto(
                        cosemObjectDefinition.getClassId(),
                        new CosemObisCodeDto(cosemObjectDefinition.getLogicalName().getA(),
                                cosemObjectDefinition.getLogicalName().getB(),
                                cosemObjectDefinition.getLogicalName().getC(),
                                cosemObjectDefinition.getLogicalName().getD(),
                                cosemObjectDefinition.getLogicalName().getE(),
                                cosemObjectDefinition.getLogicalName().getF()),
                        cosemObjectDefinition.getAttributeIndex(), cosemObjectDefinition.getDataIndex());

                cosemObjectDefinitionDtos.add(cosemObjectDefinitionDto);

            }
            builder.withPushObjectList(cosemObjectDefinitionDtos);
        }

        builder.withRandomisationStartInterval(source.getRandomisationStartInterval());
        builder.withRepetitionDelay(source.getRepetitionDelay());

        final SendDestinationAndMethod sendDestinationAndMethod = source.getSendDestinationAndMethod();
        if (sendDestinationAndMethod != null) {

            final TransportServiceType transportService = sendDestinationAndMethod.getTransportService();
            final String transportServiceName = transportService.name();
            TransportServiceTypeDto.valueOf(transportServiceName);

            final SendDestinationAndMethodDto sendDestinationAndMethodDto = new SendDestinationAndMethodDto(
                    TransportServiceTypeDto.valueOf(transportServiceName),
                    source.getSendDestinationAndMethod().getDestination(),
                    MessageTypeDto.valueOf(source.getSendDestinationAndMethod().getMessage().name()));

            builder.withSendDestinationAndMethod(sendDestinationAndMethodDto);
        }
        return builder.build();
    }
}
