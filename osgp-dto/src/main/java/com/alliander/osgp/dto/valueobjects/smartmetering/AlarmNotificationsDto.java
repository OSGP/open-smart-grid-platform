/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class AlarmNotificationsDto implements Serializable {

    private static final long serialVersionUID = 2319359505656305783L;

    private final Set<AlarmNotificationDto> alarmNotifications;

    public AlarmNotificationsDto(final Set<AlarmNotificationDto> alarmNotifications) {
        this.alarmNotifications = new TreeSet<AlarmNotificationDto>(alarmNotifications);
    }

    @Override
    public String toString() {
        return "AlarmNotifications[" + this.alarmNotifications + "]";
    }

    public Set<AlarmNotificationDto> getAlarmNotifications() {
        return Collections.unmodifiableSet(this.alarmNotifications);
    }
}
