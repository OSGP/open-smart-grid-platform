/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

public class AlarmNotifications implements Serializable {

    private static final long serialVersionUID = 2319359505656305783L;

    private Set<AlarmNotification> alarmNotifications;

    public AlarmNotifications() {
        this.alarmNotifications = Collections.emptySet();
    }

    public AlarmNotifications(final Set<AlarmNotification> alarmNotifications) {
        this.alarmNotifications = alarmNotifications;
    }

    @Override
    public String toString() {
        return "AlarmNotifications[" + String.valueOf(this.alarmNotifications) + "]";
    }

    public Set<AlarmNotification> getAlarmNotifications() {
        return this.alarmNotifications;
    }

    public void setAlarmNotifications(final Set<AlarmNotification> alarmNotifications) {
        this.alarmNotifications = alarmNotifications;
    }
}
