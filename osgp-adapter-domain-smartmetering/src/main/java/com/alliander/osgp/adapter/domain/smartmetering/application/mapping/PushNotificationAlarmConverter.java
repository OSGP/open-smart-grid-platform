/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import java.util.EnumSet;
import java.util.Set;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PushNotificationAlarm;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushNotificationAlarmDto;

public class PushNotificationAlarmConverter extends
        BidirectionalConverter<PushNotificationAlarmDto, PushNotificationAlarm> {

    @Override
    public PushNotificationAlarm convertTo(final PushNotificationAlarmDto source,
            final Type<PushNotificationAlarm> destinationType) {
        if (source == null) {
            return null;
        }

        final Set<AlarmType> alarms = EnumSet.noneOf(AlarmType.class);

        final Set<AlarmTypeDto> sourceAlarms = source.getAlarms();
        for (final AlarmTypeDto sourceAlarm : sourceAlarms) {
            alarms.add(AlarmType.valueOf(sourceAlarm.name()));
        }

        return new PushNotificationAlarm(source.getDeviceIdentification(), alarms);
    }

    @Override
    public PushNotificationAlarmDto convertFrom(final PushNotificationAlarm source,
            final Type<PushNotificationAlarmDto> destinationType) {
        if (source == null) {
            return null;
        }

        final Set<AlarmTypeDto> alarms = EnumSet.noneOf(AlarmTypeDto.class);

        final Set<AlarmType> sourceAlarms = source.getAlarms();
        for (final AlarmType sourceAlarm : sourceAlarms) {
            alarms.add(AlarmTypeDto.valueOf(sourceAlarm.name()));
        }

        return new PushNotificationAlarmDto(source.getDeviceIdentification(), alarms);
    }
}
