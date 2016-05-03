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

import com.alliander.osgp.dto.valueobjects.ConfigurationDto;
import com.alliander.osgp.oslp.Oslp;

public class ConfigurationToOslpSetConfigurationRequestConverter extends
        CustomConverter<ConfigurationDto, Oslp.SetConfigurationRequest> {
    @Override
    public Oslp.SetConfigurationRequest convert(final ConfigurationDto source,
            final Type<? extends Oslp.SetConfigurationRequest> destinationType) {
        final Oslp.SetConfigurationRequest.Builder setConfigurationRequest = Oslp.SetConfigurationRequest.newBuilder();

        if (source.getLightType() != null) {
            setConfigurationRequest.setLightType(this.mapperFacade.map(source.getLightType(), Oslp.LightType.class));
        }

        if (source.getDaliConfiguration() != null) {
            setConfigurationRequest.setDaliConfiguration(this.mapperFacade.map(source.getDaliConfiguration(),
                    Oslp.DaliConfiguration.class));
        }

        if (source.getRelayConfiguration() != null) {
            setConfigurationRequest.setRelayConfiguration(this.mapperFacade.map(source.getRelayConfiguration(),
                    Oslp.RelayConfiguration.class));
        }

        if (source.getShortTermHistoryIntervalMinutes() != null) {
            setConfigurationRequest.setShortTermHistoryIntervalMinutes(this.mapperFacade.map(
                    source.getShortTermHistoryIntervalMinutes(), Integer.class));
        }

        if (source.getLongTermHistoryInterval() != null) {
            setConfigurationRequest.setLongTermHistoryInterval(this.mapperFacade.map(
                    source.getLongTermHistoryInterval(), Integer.class));
        }

        if (source.getLongTermHistoryIntervalType() != null) {
            setConfigurationRequest.setLongTermHistoryIntervalType(this.mapperFacade.map(
                    source.getLongTermHistoryIntervalType(), Oslp.LongTermIntervalType.class));
        }

        if (source.getPreferredLinkType() != null) {
            setConfigurationRequest.setPreferredLinkType(this.mapperFacade.map(source.getPreferredLinkType(),
                    Oslp.LinkType.class));
        }

        if (source.getMeterType() != null) {
            setConfigurationRequest.setMeterType(this.mapperFacade.map(source.getMeterType(), Oslp.MeterType.class));
        }

        return setConfigurationRequest.build();
    }
}
