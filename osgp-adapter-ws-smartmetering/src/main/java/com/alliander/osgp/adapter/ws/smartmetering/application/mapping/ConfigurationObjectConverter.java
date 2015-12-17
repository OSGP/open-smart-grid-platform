/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

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
        BidirectionalConverter<ConfigurationObject, com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationObject> {

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationObject convertTo(
            final ConfigurationObject source,
            final Type<com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationObject> destinationType) {
        if (source == null) {
            return null;
        }

        final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationObject result = new com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationObject();

        final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GprsOperationModeType gprsOperationModeType = com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GprsOperationModeType
                .valueOf(source.getGprsOperationMode().name());

        final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationFlags configurationFlags = new com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationFlags();
        final List<com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationFlag> flags = configurationFlags
                .getConfigurationFlag();

        final List<ConfigurationFlag> configurationFlagList = source.getConfigurationFlags().getConfigurationFlag();
        for (final ConfigurationFlag configurationFlag : configurationFlagList) {
            final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationFlag flag = new com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationFlag();
            flag.setConfigurationFlagType(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationFlagType
                    .valueOf(configurationFlag.getConfigurationFlagType().name()));
            flag.setEnabled(configurationFlag.isEnabled());
            flags.add(flag);
        }

        result.setGprsOperationMode(gprsOperationModeType);
        result.setConfigurationFlags(configurationFlags);

        return result;
    }

    @Override
    public ConfigurationObject convertFrom(
            final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationObject source,
            final Type<ConfigurationObject> destinationType) {
        if (source == null) {
            return null;
        }

        final GprsOperationModeType gprsOperationMode = GprsOperationModeType.valueOf(source.getGprsOperationMode()
                .name());
        final List<ConfigurationFlag> configurationFlagList = new ArrayList<>();

        final List<com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationFlag> flags = source
                .getConfigurationFlags().getConfigurationFlag();
        for (final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationFlag flag : flags) {
            final ConfigurationFlagType configurationFlagType = ConfigurationFlagType.valueOf(flag
                    .getConfigurationFlagType().name());
            final boolean enabled = flag.isEnabled();
            final ConfigurationFlag configurationFlag = new ConfigurationFlag(configurationFlagType, enabled);
            configurationFlagList.add(configurationFlag);
        }

        final ConfigurationFlags configurationFlags = new ConfigurationFlags(configurationFlagList);

        return new ConfigurationObject(gprsOperationMode, configurationFlags);
    }
}
