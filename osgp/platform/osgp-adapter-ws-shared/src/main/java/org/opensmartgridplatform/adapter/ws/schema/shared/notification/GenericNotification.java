// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.schema.shared.notification;

public class GenericNotification {

  private final String message;
  private final String result;
  private final String deviceIdentification;
  private final String correlationUid;
  private final String notificationType;

  public GenericNotification(
      final String message,
      final String result,
      final String deviceIdentification,
      final String correlationUid,
      final String notificationType) {

    this.deviceIdentification = deviceIdentification;
    this.result = result;
    this.correlationUid = correlationUid;
    this.message = message;
    this.notificationType = notificationType;
  }

  /**
   * @return the message
   */
  public String getMessage() {
    return this.message;
  }

  /**
   * @return the result
   */
  public String getResult() {
    return this.result;
  }

  /**
   * @return the deviceIdentification
   */
  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  /**
   * @return the correlationUid
   */
  public String getCorrelationUid() {
    return this.correlationUid;
  }

  /**
   * @return the notificationType
   */
  public String getNotificationType() {
    return this.notificationType;
  }
}
