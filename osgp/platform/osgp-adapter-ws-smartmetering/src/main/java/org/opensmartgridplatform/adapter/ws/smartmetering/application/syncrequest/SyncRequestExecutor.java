// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.syncrequest;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.notification.NotificationType;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.opensmartgridplatform.adapter.ws.shared.services.ResponseDataService;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * SyncRequestExecutor
 *
 * <p>Provides basic functionality for handling synchronous requests as if they were asynchronous.
 * This means the response will be stored to be retrieved later and a notification will be sent to
 * indicate the response data is available.
 */
public abstract class SyncRequestExecutor {

  @Value("${sync.notification.delay}")
  private int syncNotificationDelay;

  @Autowired private NotificationService notificationService;

  @Autowired private ResponseDataService responseDataService;

  final DeviceFunction messageType;

  SyncRequestExecutor(final DeviceFunction messageType) {
    this.messageType = messageType;
  }

  ResponseMessageResultType getResultType() {
    return ResponseMessageResultType.OK;
  }

  /**
   * To be called after a request was succesfully performed. This will hande the behaviour to act as
   * a asynchronous request.
   *
   * @param data Response data
   */
  void postExecute(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final Serializable data) {
    this.storeResponseData(
        organisationIdentification,
        deviceIdentification,
        correlationUid,
        ResponseMessageResultType.OK,
        data);

    // Delay execution so the notification will not arrive before the
    // response of this call.
    new java.util.Timer()
        .schedule(
            new java.util.TimerTask() {
              @Override
              public void run() {
                SyncRequestExecutor.this.sendNotification(
                    organisationIdentification,
                    deviceIdentification,
                    correlationUid,
                    ResponseMessageResultType.OK);
              }
            },
            this.syncNotificationDelay);
  }

  /**
   * To be called when an exception occurred. This will store the exception message and send a
   * NOT_OK status notification.
   */
  void handleException(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final Exception exception) {
    this.storeResponseData(
        organisationIdentification,
        deviceIdentification,
        correlationUid,
        ResponseMessageResultType.NOT_OK,
        exception.getMessage());
    this.sendNotification(
        organisationIdentification,
        deviceIdentification,
        correlationUid,
        ResponseMessageResultType.NOT_OK);
  }

  private DeviceFunction getMessageType() {
    return this.messageType;
  }

  private void storeResponseData(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final ResponseMessageResultType resultType,
      final Serializable data) {
    final short numberOfNotificationsSent = 0;
    final CorrelationIds ids =
        new CorrelationIds(organisationIdentification, deviceIdentification, correlationUid);
    final ResponseData responseData =
        new ResponseData(
            ids, this.getMessageType().name(), resultType, data, numberOfNotificationsSent);
    this.responseDataService.enqueue(responseData);
  }

  private void sendNotification(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final ResponseMessageResultType resultType) {
    final NotificationType notificationType =
        NotificationType.valueOf(this.getMessageType().name());
    this.notificationService.sendNotification(
        organisationIdentification,
        deviceIdentification,
        resultType.name(),
        correlationUid,
        this.getNotificationMessage(),
        notificationType);
  }

  private String getNotificationMessage() {
    return String.format("Response of type %s is available.", this.getMessageType().name());
  }
}
