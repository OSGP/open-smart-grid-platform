// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support;

import org.springframework.stereotype.Component;

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
