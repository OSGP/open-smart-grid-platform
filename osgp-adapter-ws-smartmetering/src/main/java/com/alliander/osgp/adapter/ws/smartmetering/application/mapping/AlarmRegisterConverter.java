/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmRegister;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmType;

public class AlarmRegisterConverter
extends
        BidirectionalConverter<AlarmRegister, com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AlarmRegister> {

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AlarmRegister convertTo(
            final AlarmRegister source,
            final Type<com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AlarmRegister> destinationType) {
        if (source == null) {
            return null;
        }

        final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AlarmRegister result = new com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AlarmRegister();

        final List<com.alliander.osgp.adapter.ws.schema.smartmetering.common.AlarmType> alarmTypes = result
                .getAlarmTypes();

        final Set<AlarmType> sourceAlarmTypes = source.getAlarmTypes();

        for (final AlarmType sourceAlarmType : sourceAlarmTypes) {
            alarmTypes.add(com.alliander.osgp.adapter.ws.schema.smartmetering.common.AlarmType.valueOf(sourceAlarmType
                    .name()));
        }

        return result;
    }

    @Override
    public AlarmRegister convertFrom(
            final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AlarmRegister source,
            final Type<AlarmRegister> destinationType) {
        if (source == null) {
            return null;
        }

        final Set<AlarmType> alarmTypes = new TreeSet<AlarmType>();

        final List<com.alliander.osgp.adapter.ws.schema.smartmetering.common.AlarmType> sourceAlarmTypes = source
                .getAlarmTypes();

        for (final com.alliander.osgp.adapter.ws.schema.smartmetering.common.AlarmType sourceAlarmType : sourceAlarmTypes) {
            alarmTypes.add(AlarmType.valueOf(sourceAlarmType.name()));
        }

        return new AlarmRegister(alarmTypes);
    }
}
