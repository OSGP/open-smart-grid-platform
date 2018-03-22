/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters;

import java.util.ArrayList;
import java.util.List;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemObisCode;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemObjectDefinition;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupSms;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SendDestinationAndMethod;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.WindowElement;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemObisCodeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemObjectDefinitionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupSmsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SendDestinationAndMethodDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.WindowElementDto;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public class PushSetupSmsDtoConverter
        extends CustomConverter<PushSetupSms, com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupSmsDto> {

    private final ConfigurationMapper configurationMapper;

    public PushSetupSmsDtoConverter() {
        this.configurationMapper = new ConfigurationMapper();
    }

    public PushSetupSmsDtoConverter(final ConfigurationMapper mapper) {
        this.configurationMapper = mapper;
    }

    @Override
    public PushSetupSmsDto convert(final PushSetupSms source,
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
        final SendDestinationAndMethodDto a = this.mapSendDestinationMethod(source, builder, sendDestinationAndMethod);
        builder.withSendDestinationAndMethod(a);
        return builder.build();
    }

    /**
     * @param source
     * @param builder
     * @param sendDestinationAndMethod
     */
    private SendDestinationAndMethodDto mapSendDestinationMethod(final PushSetupSms source,
            final PushSetupSmsDto.Builder builder, final SendDestinationAndMethod sendDestinationAndMethod) {
        if (sendDestinationAndMethod != null) {
            return this.configurationMapper.map(sendDestinationAndMethod, SendDestinationAndMethodDto.class);
        }
        return null;
    }
}
