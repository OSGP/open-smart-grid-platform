// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.AlarmType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.AlarmNotification;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.AlarmNotifications;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsRequestData;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SetAlarmNotificationsRequestFactory {

  public static final int NUMBER_ALARM_TYPES = 32;

  private SetAlarmNotificationsRequestFactory() {
    // Private constructor for utility class
  }

  public static SetAlarmNotificationsRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final SetAlarmNotificationsRequest setAlarmNotificationsRequest =
        new SetAlarmNotificationsRequest();
    setAlarmNotificationsRequest.setDeviceIdentification(
        requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));

    setAlarmNotificationsRequest.setSetAlarmNotificationsRequestData(
        fetchAlarmNotifications(requestParameters));

    return setAlarmNotificationsRequest;
  }

  public static SetAlarmNotificationsAsyncRequest fromScenarioContext() {
    final SetAlarmNotificationsAsyncRequest setAlarmNotificationsAsyncRequest =
        new SetAlarmNotificationsAsyncRequest();
    setAlarmNotificationsAsyncRequest.setCorrelationUid(
        RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    setAlarmNotificationsAsyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return setAlarmNotificationsAsyncRequest;
  }

  private static SetAlarmNotificationsRequestData fetchAlarmNotifications(
      final Map<String, String> requestParameters) {
    final List<AlarmNotification> alarmNotificationsList = new ArrayList<>();

    if (nullCheckAlarmType(requestParameters, "")) {
      alarmNotificationsList.add(getAlarmNotification(requestParameters, ""));
    }
    for (int i = 1; i < NUMBER_ALARM_TYPES; i++) {
      if (nullCheckAlarmType(requestParameters, "_" + Integer.toString(i))) {
        alarmNotificationsList.add(
            getAlarmNotification(requestParameters, "_" + Integer.toString(i)));
      }
    }

    return addAlarmNotificationsToRequestData(alarmNotificationsList);
  }

  private static SetAlarmNotificationsRequestData addAlarmNotificationsToRequestData(
      final List<AlarmNotification> lstAlarmNotifications) {
    final AlarmNotifications alarmNotifications = new AlarmNotifications();
    alarmNotifications.getAlarmNotification().addAll(lstAlarmNotifications);

    final SetAlarmNotificationsRequestData setAlarmNotificationsRequestData =
        new SetAlarmNotificationsRequestData();
    setAlarmNotificationsRequestData.setAlarmNotifications(alarmNotifications);

    return setAlarmNotificationsRequestData;
  }

  private static AlarmNotification getAlarmNotification(
      final Map<String, String> requestParameters, final String postfix) {
    final AlarmNotification alarmNotification = new AlarmNotification();
    alarmNotification.setAlarmType(
        AlarmType.valueOf(requestParameters.get(PlatformSmartmeteringKeys.ALARM_TYPE + postfix)));
    alarmNotification.setEnabled(
        Boolean.parseBoolean(
            requestParameters.get(PlatformSmartmeteringKeys.ALARM_TYPE_ENABLED + postfix)));

    return alarmNotification;
  }

  private static boolean nullCheckAlarmType(
      final Map<String, String> requestParameters, final String postfix) {
    return requestParameters.get(PlatformSmartmeteringKeys.ALARM_TYPE + postfix) != null;
  }
}
