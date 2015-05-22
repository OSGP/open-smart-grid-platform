/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.dto.valueobjects.Configuration;
import com.alliander.osgp.dto.valueobjects.DaliConfiguration;
import com.alliander.osgp.dto.valueobjects.LightType;
import com.alliander.osgp.dto.valueobjects.LinkType;
import com.alliander.osgp.dto.valueobjects.LongTermIntervalType;
import com.alliander.osgp.dto.valueobjects.MeterType;
import com.alliander.osgp.dto.valueobjects.RelayConfiguration;
import com.alliander.osgp.oslp.Oslp;

public class OslpGetConfigurationResponseToConfigurationConverter extends
        CustomConverter<Oslp.GetConfigurationResponse, Configuration> {
    @Override
    public Configuration convert(final Oslp.GetConfigurationResponse source,
            final Type<? extends Configuration> destinationType) {
        return new Configuration(source.hasLightType() ? this.mapperFacade.map(source.getLightType(), LightType.class)
                : null, source.hasDaliConfiguration() ? this.mapperFacade.map(source.getDaliConfiguration(),
                DaliConfiguration.class) : null, source.hasRelayConfiguration() ? this.mapperFacade.map(
                source.getRelayConfiguration(), RelayConfiguration.class) : null,
                source.hasShortTermHistoryIntervalMinutes() ? this.mapperFacade.map(
                        source.getShortTermHistoryIntervalMinutes(), Integer.class) : null,
                source.hasPreferredLinkType() ? this.mapperFacade.map(source.getPreferredLinkType(), LinkType.class)
                        : null, source.hasMeterType() ? this.mapperFacade.map(source.getMeterType(), MeterType.class)
                        : null, source.hasLongTermHistoryInterval() ? this.mapperFacade.map(
                        source.getLongTermHistoryInterval(), Integer.class) : null,
                source.hasLongTermHistoryIntervalType() ? this.mapperFacade.map(
                        source.getLongTermHistoryIntervalType(), LongTermIntervalType.class) : null);
    }
}
