/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.AlarmType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotification;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotifications;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsRequestData;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SetAlarmNotificationsRequestFactory {
    private SetAlarmNotificationsRequestFactory() {
        // Private constructor for utility class
    }

    public static SetAlarmNotificationsRequest fromParameterMap(final Map<String, String> requestParameters) {
        final SetAlarmNotificationsRequest setAlarmNotificationsRequest = new SetAlarmNotificationsRequest();
        setAlarmNotificationsRequest
                .setDeviceIdentification(requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));

        setAlarmNotificationsRequest.setSetAlarmNotificationsRequestData(fetchAlarmNotifications(requestParameters));

        return setAlarmNotificationsRequest;
    }

    public static SetAlarmNotificationsAsyncRequest fromScenarioContext() {
        final SetAlarmNotificationsAsyncRequest setAlarmNotificationsAsyncRequest = new SetAlarmNotificationsAsyncRequest();
        setAlarmNotificationsAsyncRequest
                .setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
        setAlarmNotificationsAsyncRequest
                .setDeviceIdentification(RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
        return setAlarmNotificationsAsyncRequest;
    }

    private static SetAlarmNotificationsRequestData fetchAlarmNotifications(
            final Map<String, String> requestParameters) {
        final AlarmNotification alarmNotification = new AlarmNotification();
        alarmNotification.setAlarmType(AlarmType.valueOf(requestParameters.get(PlatformSmartmeteringKeys.ALARM_TYPE)));
        alarmNotification
                .setEnabled(Boolean.parseBoolean(requestParameters.get(PlatformSmartmeteringKeys.ALARM_TYPE_ENABLED)));

        final List<AlarmNotification> lstAlarmNotifications = new ArrayList<>();
        lstAlarmNotifications.add(alarmNotification);

        final AlarmNotifications alarmNotifications = new AlarmNotifications();
        alarmNotifications.getAlarmNotification().addAll(lstAlarmNotifications);

        final SetAlarmNotificationsRequestData setAlarmNotificationsRequestData = new SetAlarmNotificationsRequestData();
        setAlarmNotificationsRequestData.setAlarmNotifications(alarmNotifications);

        return setAlarmNotificationsRequestData;
    }

}
