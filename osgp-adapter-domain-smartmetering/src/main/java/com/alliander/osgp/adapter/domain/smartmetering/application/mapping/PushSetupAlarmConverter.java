/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import java.util.List;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupAlarm;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemObisCodeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemObjectDefinitionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupAlarmDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SendDestinationAndMethodDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.WindowElementDto;

public class PushSetupAlarmConverter extends CustomConverter<PushSetupAlarm, PushSetupAlarmDto> {

    @Override
    public PushSetupAlarmDto convert(final PushSetupAlarm source,
            final Type<? extends PushSetupAlarmDto> destinationType) {
        if (source == null) {
            return null;
        }

        final PushSetupAlarmDto.Builder builder = new PushSetupAlarmDto.Builder();

        builder.logicalName(this.mapperFacade.map(source.getLogicalName(), CosemObisCodeDto.class));
        if (source.hasPushObjectList()) {
            final List<CosemObjectDefinitionDto> pushObjectList = this.mapperFacade.mapAsList(
                    source.getPushObjectList(), CosemObjectDefinitionDto.class);

            builder.pushObjectList(pushObjectList);
        }
        builder.sendDestinationAndMethod(this.mapperFacade.map(source.getSendDestinationAndMethod(),
                SendDestinationAndMethodDto.class));
        if (source.hasCommunicationWindow()) {
            final List<WindowElementDto> communicationWindow = this.mapperFacade.mapAsList(
                    source.getCommunicationWindow(), WindowElementDto.class);

            builder.communicationWindow(communicationWindow);
        }
        builder.randomisationStartInterval(source.getRandomisationStartInterval());
        builder.numberOfRetries(source.getNumberOfRetries());
        builder.repetitionDelay(source.getRepetitionDelay());

        return builder.build();
    }
}
