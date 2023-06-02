//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class SetAlarmNotificationsRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = -1833812559523610412L;

  private final AlarmNotificationsDto alarmNotifications;

  public SetAlarmNotificationsRequestDto(final AlarmNotificationsDto alarmNotifications) {
    super();
    this.alarmNotifications = alarmNotifications;
  }

  public AlarmNotificationsDto getAlarmNotifications() {
    return this.alarmNotifications;
  }
}
