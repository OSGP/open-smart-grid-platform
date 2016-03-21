/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import java.util.ArrayList;
import java.util.List;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.ConfigurationFlag;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ConfigurationFlagType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ConfigurationFlags;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ConfigurationObject;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GprsOperationModeType;

public class ConfigurationObjectConverter
        extends
        BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationObjectDto, ConfigurationObject> {

    @Override
    public ConfigurationObject convertTo(
            final com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationObjectDto source,
            final Type<ConfigurationObject> destinationType) {
        if (source == null) {
            return null;
        }

        final GprsOperationModeType gprsOperationMode = GprsOperationModeType.valueOf(source.getGprsOperationMode()
                .name());

        final List<ConfigurationFlag> configurationFlagList = new ArrayList<>();
        final List<com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlagDto> flags = source
                .getConfigurationFlags().getConfigurationFlag();
        for (final com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlagDto flag : flags) {
            final ConfigurationFlagType configurationFlagType = ConfigurationFlagType.valueOf(flag
                    .getConfigurationFlagType().name());
            final boolean enabled = flag.isEnabled();
            final ConfigurationFlag configurationFlag = new ConfigurationFlag(configurationFlagType, enabled);
            configurationFlagList.add(configurationFlag);
        }
        final ConfigurationFlags configurationFlags = new ConfigurationFlags(configurationFlagList);

        return new ConfigurationObject(gprsOperationMode, configurationFlags);
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationObjectDto convertFrom(
            final ConfigurationObject source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationObjectDto> destinationType) {
        if (source == null) {
            return null;
        }

        final com.alliander.osgp.dto.valueobjects.smartmetering.GprsOperationModeTypeDto gprsOperationMode = com.alliander.osgp.dto.valueobjects.smartmetering.GprsOperationModeTypeDto
                .valueOf(source.getGprsOperationMode().name());

        final List<com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlagDto> configurationFlagList = new ArrayList<>();
        final List<ConfigurationFlag> flags = source.getConfigurationFlags().getConfigurationFlag();
        for (final ConfigurationFlag flag : flags) {
            final com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto configurationFlagType = com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto
                    .valueOf(flag.getConfigurationFlagType().name());
            final boolean enabled = flag.isEnabled();
            final com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlagDto configurationFlag = new com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlagDto(
                    configurationFlagType, enabled);
            configurationFlagList.add(configurationFlag);
        }
        final com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlagsDto configurationFlags = new com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlagsDto(
                configurationFlagList);

        return new com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationObjectDto(gprsOperationMode,
                configurationFlags);
    }
}