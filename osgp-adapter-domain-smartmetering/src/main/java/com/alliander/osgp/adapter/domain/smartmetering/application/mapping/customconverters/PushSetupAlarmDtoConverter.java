/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupAlarm;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupAlarmDto;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public class PushSetupAlarmDtoConverter extends AbstractPushSetupConverter<PushSetupAlarm, PushSetupAlarmDto> {

    public PushSetupAlarmDtoConverter(final ConfigurationMapper configurationMapper) {
        super(configurationMapper);
    }

    @Override
    public PushSetupAlarmDto convert(final PushSetupAlarm source,
            final Type<? extends com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupAlarmDto> destinationType,
            final MappingContext context) {

        /*
         * Cast to PushSetupAlarmDto should be fine, because the builder
         * returned from newBuilder is a PushSetupAlarmDto.Builder, which builds
         * a PushSetupAlarmDto.
         */
        return (PushSetupAlarmDto) super.convert(source);
    }

    /*
     * This more specific return type should be fine as
     * PushSetupAlarmDto.Builder extends
     * AbstractBuilder<PushSetupAlarmDto.Builder>
     */
    @SuppressWarnings("unchecked")
    @Override
    protected PushSetupAlarmDto.Builder newBuilder() {
        return new PushSetupAlarmDto.Builder();
    }
}
