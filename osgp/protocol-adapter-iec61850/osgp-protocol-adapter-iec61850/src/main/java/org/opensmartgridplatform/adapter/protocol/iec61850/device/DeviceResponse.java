//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.device;

public class DeviceResponse {

  private final String organisationIdentification;
  private final String deviceIdentification;
  private final String correlationUid;
  private final int messagePriority;

  public DeviceResponse(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final int messagePriority) {
    this.organisationIdentification = organisationIdentification;
    this.deviceIdentification = deviceIdentification;
    this.correlationUid = correlationUid;
    this.messagePriority = messagePriority;
  }

  public String getOrganisationIdentification() {
    return this.organisationIdentification;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public String getCorrelationUid() {
    return this.correlationUid;
  }

  public int getMessagePriority() {
    return this.messagePriority;
  }
}
