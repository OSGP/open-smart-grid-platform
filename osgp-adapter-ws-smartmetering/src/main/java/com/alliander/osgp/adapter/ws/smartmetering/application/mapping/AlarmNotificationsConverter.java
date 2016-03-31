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

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotification;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotifications;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmType;

public class AlarmNotificationsConverter
extends
        BidirectionalConverter<AlarmNotifications, com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotifications> {

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotifications convertTo(
            final AlarmNotifications source,
            final Type<com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotifications> destinationType) {
        if (source == null) {
            return null;
        }

        final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotifications result = new com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotifications();

        final List<com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotification> alarmNotifications = result
                .getAlarmNotification();

        final Set<AlarmNotification> sourceNotifications = source.getAlarmNotificationsSet();

        for (final AlarmNotification sourceNotification : sourceNotifications) {

            final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotification alarmNotification = new com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotification();

            alarmNotification.setAlarmType(com.alliander.osgp.adapter.ws.schema.smartmetering.common.AlarmType
                    .valueOf(sourceNotification.getAlarmType().name()));
            alarmNotification.setEnabled(sourceNotification.isEnabled());

            alarmNotifications.add(alarmNotification);
        }

        return result;
    }

    @Override
    public AlarmNotifications convertFrom(
            final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotifications source,
            final Type<AlarmNotifications> destinationType) {
        if (source == null) {
            return null;
        }

        final Set<AlarmNotification> alarmNotifications = new TreeSet<AlarmNotification>();

        final List<com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotification> sourceNotifications = source
                .getAlarmNotification();

        for (final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotification sourceNotification : sourceNotifications) {

            final AlarmType alarmType = AlarmType.valueOf(sourceNotification.getAlarmType().name());
            final boolean enabled = sourceNotification.isEnabled();

            alarmNotifications.add(new AlarmNotification(alarmType, enabled));
        }

        return new AlarmNotifications(alarmNotifications);
    }
}
