/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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

  /** @return the message */
  public String getMessage() {
    return this.message;
  }

  /** @return the result */
  public String getResult() {
    return this.result;
  }

  /** @return the deviceIdentification */
  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  /** @return the correlationUid */
  public String getCorrelationUid() {
    return this.correlationUid;
  }

  /** @return the notificationType */
  public String getNotificationType() {
    return this.notificationType;
  }
}
