/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemObisCode;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemObjectDefinition;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupAlarm;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SendDestinationAndMethod;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.WindowElement;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemObisCodeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemObjectDefinitionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupAlarmDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SendDestinationAndMethodDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.WindowElementDto;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public class PushSetupAlarmDtoConverter extends CustomConverter<PushSetupAlarm, PushSetupAlarmDto> {

    private final ConfigurationMapper configurationMapper;

    public PushSetupAlarmDtoConverter() {
        this.configurationMapper = new ConfigurationMapper();
    }

    public PushSetupAlarmDtoConverter(final ConfigurationMapper configurationMapper) {
        this.configurationMapper = configurationMapper;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof PushSetupAlarmDtoConverter)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        final PushSetupAlarmDtoConverter o = (PushSetupAlarmDtoConverter) other;
        if (this.configurationMapper == null) {
            return o.configurationMapper == null;
        }
        return this.configurationMapper.getClass().equals(o.configurationMapper.getClass());
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hashCode(this.configurationMapper);
    }

    @Override
    public PushSetupAlarmDto convert(final PushSetupAlarm source,
            final Type<? extends com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupAlarmDto> destinationType,
            final MappingContext context) {

        if (source == null) {
            return null;
        }

        final PushSetupAlarmDto.Builder builder = new PushSetupAlarmDto.Builder();

        final List<WindowElementDto> windowElementDtos = new ArrayList<>();
        final List<WindowElement> communicationWindows = source.getCommunicationWindow();
        if (communicationWindows != null) {
            for (final WindowElement element : communicationWindows) {
                final WindowElementDto windowElementDto = this.configurationMapper.map(element, WindowElementDto.class);
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
        final List<CosemObjectDefinition> pushObjectLists = source.getPushObjectList();
        if (pushObjectLists != null) {
            for (final CosemObjectDefinition cosemObjectDefinition : pushObjectLists) {
                final CosemObjectDefinitionDto cosemObjectDefinitionDto = this.configurationMapper
                        .map(cosemObjectDefinition, CosemObjectDefinitionDto.class);
                cosemObjectDefinitionDtos.add(cosemObjectDefinitionDto);
            }
            builder.withPushObjectList(cosemObjectDefinitionDtos);
        }

        builder.withRandomisationStartInterval(source.getRandomisationStartInterval());
        builder.withRepetitionDelay(source.getRepetitionDelay());

        final SendDestinationAndMethod sendDestinationAndMethod = source.getSendDestinationAndMethod();
        final SendDestinationAndMethodDto sendDestinationAndMethodDto = this
                .mapSendDestinationMethod(sendDestinationAndMethod);
        builder.withSendDestinationAndMethod(sendDestinationAndMethodDto);
        return builder.build();
    }

    /**
     * @param sendDestinationAndMethod
     */
    private SendDestinationAndMethodDto mapSendDestinationMethod(
            final SendDestinationAndMethod sendDestinationAndMethod) {
        if (sendDestinationAndMethod != null) {
            return this.configurationMapper.map(sendDestinationAndMethod, SendDestinationAndMethodDto.class);
        }
        return null;
    }
}
