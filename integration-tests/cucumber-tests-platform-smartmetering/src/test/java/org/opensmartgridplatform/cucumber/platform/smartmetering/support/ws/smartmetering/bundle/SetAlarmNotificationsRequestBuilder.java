/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetAlarmNotificationsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.AlarmType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.AlarmNotification;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.AlarmNotifications;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class SetAlarmNotificationsRequestBuilder {

  private static final int DEFAULT_ALARM_NOTIFICATIONS_COUNT = 1;
  private static final AlarmType DEFAULT_ALARM_TYPE = AlarmType.POWER_UP;
  private static final boolean DEFAULT_ENABLED = true;

  private List<AlarmNotification> alarmNotifications = new ArrayList<>();

  public SetAlarmNotificationsRequestBuilder withDefaults() {
    this.alarmNotifications = new ArrayList<>();
    this.alarmNotifications.add(this.getDefaultAlarmNotification());
    return this;
  }

  public SetAlarmNotificationsRequestBuilder fromParameterMap(
      final Map<String, String> parameters) {
    this.alarmNotifications = new ArrayList<>();
    final int alarmNotificationCount = this.getAlarmNotificationCount(parameters);
    for (int i = 1; i <= alarmNotificationCount; i++) {
      this.alarmNotifications.add(this.getAlarmNotification(parameters, i));
    }
    return this;
  }

  public SetAlarmNotificationsRequest build() {
    final SetAlarmNotificationsRequest request = new SetAlarmNotificationsRequest();
    final AlarmNotifications alarmNotifications = new AlarmNotifications();
    alarmNotifications.getAlarmNotification().addAll(this.alarmNotifications);
    request.setAlarmNotifications(alarmNotifications);
    return request;
  }

  private int getAlarmNotificationCount(final Map<String, String> parameters) {
    return getInteger(
        parameters,
        PlatformSmartmeteringKeys.ALARM_NOTIFICATION_COUNT,
        DEFAULT_ALARM_NOTIFICATIONS_COUNT);
  }

  private AlarmNotification getDefaultAlarmNotification() {
    final AlarmNotification alarmNotification = new AlarmNotification();
    alarmNotification.setAlarmType(DEFAULT_ALARM_TYPE);
    alarmNotification.setEnabled(true);
    return alarmNotification;
  }

  private AlarmNotification getAlarmNotification(
      final Map<String, String> parameters, final int index) {
    final AlarmNotification alarmNotification = new AlarmNotification();
    alarmNotification.setAlarmType(this.getAlarmType(parameters, index));
    alarmNotification.setEnabled(this.getEnabled(parameters, index));
    return alarmNotification;
  }

  private AlarmType getAlarmType(final Map<String, String> parameters, final int index) {
    final String key = SettingsHelper.makeKey(PlatformSmartmeteringKeys.ALARM_TYPE, index);
    return getEnum(parameters, key, AlarmType.class, DEFAULT_ALARM_TYPE);
  }

  private boolean getEnabled(final Map<String, String> parameters, final int index) {
    final String key = SettingsHelper.makeKey(PlatformSmartmeteringKeys.ALARM_TYPE_ENABLED, index);
    return getBoolean(parameters, key, DEFAULT_ENABLED);
  }
}
