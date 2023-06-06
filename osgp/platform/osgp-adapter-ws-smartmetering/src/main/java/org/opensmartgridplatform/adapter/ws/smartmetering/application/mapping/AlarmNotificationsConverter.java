// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmNotification;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmNotifications;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmType;

public class AlarmNotificationsConverter
    extends BidirectionalConverter<
        AlarmNotifications,
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .AlarmNotifications> {

  @Override
  public org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.AlarmNotifications
      convertTo(
          final AlarmNotifications source,
          final Type<
                  org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                      .AlarmNotifications>
              destinationType,
          final MappingContext context) {
    if (source == null) {
      return null;
    }

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.AlarmNotifications
        result =
            new org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                .AlarmNotifications();

    final List<
            org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                .AlarmNotification>
        alarmNotifications = result.getAlarmNotification();

    final Set<AlarmNotification> sourceNotifications = source.getAlarmNotificationsSet();

    for (final AlarmNotification sourceNotification : sourceNotifications) {

      final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
              .AlarmNotification
          alarmNotification =
              new org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                  .AlarmNotification();

      alarmNotification.setAlarmType(
          org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.AlarmType.valueOf(
              sourceNotification.getAlarmType().name()));
      alarmNotification.setEnabled(sourceNotification.isEnabled());

      alarmNotifications.add(alarmNotification);
    }

    return result;
  }

  @Override
  public AlarmNotifications convertFrom(
      final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
              .AlarmNotifications
          source,
      final Type<AlarmNotifications> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    final Set<AlarmNotification> alarmNotifications = new TreeSet<AlarmNotification>();

    final List<
            org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                .AlarmNotification>
        sourceNotifications = source.getAlarmNotification();

    for (final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .AlarmNotification
        sourceNotification : sourceNotifications) {

      final AlarmType alarmType = AlarmType.valueOf(sourceNotification.getAlarmType().name());
      final boolean enabled = sourceNotification.isEnabled();

      alarmNotifications.add(new AlarmNotification(alarmType, enabled));
    }

    return new AlarmNotifications(alarmNotifications);
  }
}
