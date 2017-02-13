/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.support;

import org.springframework.stereotype.Component;

@Deprecated
@Component
public class ServiceEndpoint {
    private String serviceEndpoint;
    private String alarmNotificationsHost;
    private int alarmNotificationsPort;

    public void setServiceEndpoint(final String serviceEndpoint) {
        this.serviceEndpoint = serviceEndpoint;
    }

    public String getServiceEndpoint() {
        return this.serviceEndpoint;
    }

    public String getAlarmNotificationsHost() {
        return this.alarmNotificationsHost;
    }

    public void setAlarmNotificationsHost(final String alarmNotificationsHost) {
        this.alarmNotificationsHost = alarmNotificationsHost;
    }

    public int getAlarmNotificationsPort() {
        return this.alarmNotificationsPort;
    }

    public void setAlarmNotificationsPort(final int alarmNotificationsPort) {
        this.alarmNotificationsPort = alarmNotificationsPort;
    }
}