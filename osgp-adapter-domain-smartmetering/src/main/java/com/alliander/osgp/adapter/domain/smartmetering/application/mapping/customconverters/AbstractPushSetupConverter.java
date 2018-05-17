/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AbstractPushSetup;
import com.alliander.osgp.dto.valueobjects.smartmetering.AbstractPushSetupDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemObisCodeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemObjectDefinitionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SendDestinationAndMethodDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.WindowElementDto;

import ma.glasnost.orika.CustomConverter;

/*
 * squid:S2160 Subclasses that add fields should override "equals"
 *
 * Following the advice for this rule would violate the Liskov substitution principle.
 */
@SuppressWarnings("squid:S2160")
public abstract class AbstractPushSetupConverter<S, D> extends CustomConverter<S, D> {

    private final ConfigurationMapper configurationMapper;

    protected AbstractPushSetupConverter(final ConfigurationMapper configurationMapper) {
        this.configurationMapper = configurationMapper;
    }

    public void configureBuilder(final AbstractPushSetupDto.AbstractBuilder<?> builder,
            final AbstractPushSetup pushSetup) {

        if (pushSetup.getCommunicationWindow() != null) {
            builder.withCommunicationWindow(
                    this.configurationMapper.mapAsList(pushSetup.getCommunicationWindow(), WindowElementDto.class));
        }
        builder.withLogicalName(this.configurationMapper.map(pushSetup.getLogicalName(), CosemObisCodeDto.class));
        builder.withNumberOfRetries(pushSetup.getNumberOfRetries());
        if (pushSetup.getPushObjectList() != null) {
            builder.withPushObjectList(
                    this.configurationMapper.mapAsList(pushSetup.getPushObjectList(), CosemObjectDefinitionDto.class));
        }
        builder.withRandomisationStartInterval(pushSetup.getRandomisationStartInterval());
        builder.withRepetitionDelay(pushSetup.getRepetitionDelay());
        builder.withSendDestinationAndMethod(this.configurationMapper.map(pushSetup.getSendDestinationAndMethod(),
                SendDestinationAndMethodDto.class));
    }

}
