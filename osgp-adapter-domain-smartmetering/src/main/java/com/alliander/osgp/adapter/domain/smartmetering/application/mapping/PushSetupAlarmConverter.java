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

public class PushSetupAlarmConverter extends
CustomConverter<PushSetupAlarm, com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupAlarm> {

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupAlarm convert(final PushSetupAlarm source,
            final Type<? extends com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupAlarm> destinationType) {
        if (source == null) {
            return null;
        }

        final com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupAlarm.Builder builder = new com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupAlarm.Builder();

        builder.logicalName(this.mapperFacade.map(source.getLogicalName(),
                com.alliander.osgp.dto.valueobjects.smartmetering.CosemObisCode.class));
        if (source.hasPushObjectList()) {
            final List<com.alliander.osgp.dto.valueobjects.smartmetering.CosemObjectDefinition> pushObjectList = this.mapperFacade
                    .mapAsList(source.getPushObjectList(),
                            com.alliander.osgp.dto.valueobjects.smartmetering.CosemObjectDefinition.class);

            builder.pushObjectList(pushObjectList);
        }
        builder.sendDestinationAndMethod(this.mapperFacade.map(source.getSendDestinationAndMethod(),
                com.alliander.osgp.dto.valueobjects.smartmetering.SendDestinationAndMethod.class));
        if (source.hasCommunicationWindow()) {
            final List<com.alliander.osgp.dto.valueobjects.smartmetering.WindowElement> communicationWindow = this.mapperFacade
                    .mapAsList(source.getCommunicationWindow(),
                            com.alliander.osgp.dto.valueobjects.smartmetering.WindowElement.class);

            builder.communicationWindow(communicationWindow);
        }
        builder.randomisationStartInterval(source.getRandomisationStartInterval());
        builder.numberOfRetries(source.getNumberOfRetries());
        builder.repetitionDelay(source.getRepetitionDelay());

        return builder.build();
    }
}
