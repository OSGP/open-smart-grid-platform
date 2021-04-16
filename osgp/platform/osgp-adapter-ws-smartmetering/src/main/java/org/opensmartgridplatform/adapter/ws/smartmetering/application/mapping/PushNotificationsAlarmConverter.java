/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import java.util.List;
import java.util.Set;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.AlarmType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.AlarmRegister;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.RetrievePushNotificationAlarmResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushNotificationAlarm;

public class PushNotificationsAlarmConverter
    extends CustomConverter<PushNotificationAlarm, RetrievePushNotificationAlarmResponse> {

  @Override
  public RetrievePushNotificationAlarmResponse convert(
      final PushNotificationAlarm source,
      final Type<? extends RetrievePushNotificationAlarmResponse> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    final RetrievePushNotificationAlarmResponse response =
        new RetrievePushNotificationAlarmResponse();
    response.setDeviceIdentification(source.getDeviceIdentification());
    final AlarmRegister alarmRegister = new AlarmRegister();
    final List<AlarmType> alarmTypes = alarmRegister.getAlarmTypes();
    final Set<org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmType> alarms =
        source.getAlarms();
    for (final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmType alarm :
        alarms) {
      alarmTypes.add(AlarmType.valueOf(alarm.name()));
    }
    response.setAlarmRegister(alarmRegister);

    return response;
  }
}
