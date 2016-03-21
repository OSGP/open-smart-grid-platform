/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import java.util.Set;
import java.util.TreeSet;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotification;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotifications;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmType;

public class AlarmNotificationsConverter
extends
BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotificationsDto, AlarmNotifications> {

    @Override
    public AlarmNotifications convertTo(
            final com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotificationsDto source,
            final Type<AlarmNotifications> destinationType) {
        if (source == null) {
            return null;
        }

        final Set<AlarmNotification> alarmNotifications = new TreeSet<AlarmNotification>();

        final Set<com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotificationDto> sourceNotifications = source
                .getAlarmNotifications();
        for (final com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotificationDto sourceNotification : sourceNotifications) {
            final AlarmType alarmType = AlarmType.valueOf(sourceNotification.getAlarmType().name());
            final boolean enabled = sourceNotification.isEnabled();
            final AlarmNotification alarmNotification = new AlarmNotification(alarmType, enabled);
            alarmNotifications.add(alarmNotification);
        }

        return new AlarmNotifications(alarmNotifications);
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotificationsDto convertFrom(
            final AlarmNotifications source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotificationsDto> destinationType) {
        if (source == null) {
            return null;
        }

        final Set<com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotificationDto> alarmNotifications = new TreeSet<com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotificationDto>();

        final Set<AlarmNotification> sourceNotifications = source.getAlarmNotifications();

        for (final AlarmNotification sourceNotification : sourceNotifications) {
            final com.alliander.osgp.dto.valueobjects.smartmetering.AlarmTypeDto alarmType = com.alliander.osgp.dto.valueobjects.smartmetering.AlarmTypeDto
                    .valueOf(sourceNotification.getAlarmType().name());
            final boolean enabled = sourceNotification.isEnabled();
            final com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotificationDto alarmNotification = new com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotificationDto(
                    alarmType, enabled);
            alarmNotifications.add(alarmNotification);
        }

        return new com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotificationsDto(alarmNotifications);
    }
}