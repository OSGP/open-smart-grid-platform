/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.application.mapping;

import com.alliander.osgp.dto.valueobjects.ConfigurationDto;
import com.alliander.osgp.dto.valueobjects.DaliConfigurationDto;
import com.alliander.osgp.dto.valueobjects.LightTypeDto;
import com.alliander.osgp.dto.valueobjects.LinkTypeDto;
import com.alliander.osgp.dto.valueobjects.LongTermIntervalTypeDto;
import com.alliander.osgp.dto.valueobjects.MeterTypeDto;
import com.alliander.osgp.dto.valueobjects.RelayConfigurationDto;
import com.alliander.osgp.oslp.Oslp;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public class OslpGetConfigurationResponseToConfigurationConverter
        extends CustomConverter<Oslp.GetConfigurationResponse, ConfigurationDto> {
    @Override
    public ConfigurationDto convert(final Oslp.GetConfigurationResponse source,
            final Type<? extends ConfigurationDto> destinationType, final MappingContext context) {
        return new ConfigurationDto(
                source.hasLightType() ? this.mapperFacade.map(source.getLightType(), LightTypeDto.class) : null,
                source.hasDaliConfiguration()
                        ? this.mapperFacade.map(source.getDaliConfiguration(), DaliConfigurationDto.class) : null,
                source.hasRelayConfiguration()
                        ? this.mapperFacade.map(source.getRelayConfiguration(), RelayConfigurationDto.class) : null,
                source.hasShortTermHistoryIntervalMinutes()
                        ? this.mapperFacade.map(source.getShortTermHistoryIntervalMinutes(), Integer.class) : null,
                source.hasPreferredLinkType() ? this.mapperFacade.map(source.getPreferredLinkType(), LinkTypeDto.class)
                        : null,
                source.hasMeterType() ? this.mapperFacade.map(source.getMeterType(), MeterTypeDto.class) : null,
                source.hasLongTermHistoryInterval()
                        ? this.mapperFacade.map(source.getLongTermHistoryInterval(), Integer.class) : null,
                source.hasLongTermHistoryIntervalType()
                        ? this.mapperFacade.map(source.getLongTermHistoryIntervalType(), LongTermIntervalTypeDto.class)
                        : null);
    }
}
