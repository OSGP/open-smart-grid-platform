/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class NotificationDto implements Serializable {

  private static final long serialVersionUID = 4424128385545688694L;

  private String message;
  private String result;
  private String deviceIdentification;
  private String correlationUid;

  protected NotificationTypeDto notificationType;

  public NotificationDto(
      final String message,
      final String result,
      final String deviceIdentification,
      final String correlationUid,
      final NotificationTypeDto notificationType) {
    this.message = message;
    this.result = result;
    this.deviceIdentification = deviceIdentification;
    this.correlationUid = correlationUid;
    this.notificationType = notificationType;
  }

  public String getMessage() {
    return this.message;
  }

  public String getResult() {
    return this.result;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public String getCorrelationUid() {
    return this.correlationUid;
  }

  public NotificationTypeDto getNotificationType() {
    return this.notificationType;
  }
}
