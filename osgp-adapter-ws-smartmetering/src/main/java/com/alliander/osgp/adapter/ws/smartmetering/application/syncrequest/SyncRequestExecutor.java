/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.syncrequest;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.NotificationType;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.MeterResponseDataService;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.NotificationService;
import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.MeterResponseData;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

/**
 * SyncRequestExecutor
 *
 * Provides basic functionality for handling synchronous requests as if they
 * were asynchronous. This means the response will be stored to be retrieved
 * later and a notification will be sent to indicate the response data is
 * available.
 *
 */
public abstract class SyncRequestExecutor {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MeterResponseDataService meterResponseDataService;

    final DeviceFunction messageType;

    SyncRequestExecutor(final DeviceFunction messageType) {
        this.messageType = messageType;
    }

    ResponseMessageResultType getResultType() {
        return ResponseMessageResultType.OK;
    }

    /**
     * To be called after a request was succesfully performed. This will hande
     * the behaviour to act as a asynchronous request.
     *
     * @param organisationIdentification
     * @param deviceIdentification
     * @param correlationUid
     * @param data
     *            Response data
     */
    void postExecute(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final Serializable data) {
        this.storeMeterResponseData(organisationIdentification, deviceIdentification, correlationUid,
                ResponseMessageResultType.OK, data);
        this.sendNotification(organisationIdentification, deviceIdentification, correlationUid,
                ResponseMessageResultType.OK);
    }

    /**
     * To be called when an exception occurred. This will store the exception
     * message and send a NOT_OK status notification.
     *
     * @param organisationIdentification
     * @param deviceIdentification
     * @param correlationUid
     * @param exception
     */
    void handleException(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final Exception exception) {
        this.storeMeterResponseData(organisationIdentification, deviceIdentification, correlationUid,
                ResponseMessageResultType.NOT_OK, exception.getMessage());
        this.sendNotification(organisationIdentification, deviceIdentification, correlationUid,
                ResponseMessageResultType.NOT_OK);
    }

    private DeviceFunction getMessageType() {
        return messageType;
    }

    private void storeMeterResponseData(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final ResponseMessageResultType resultType, final Serializable data) {
        final MeterResponseData meterResponseData = new MeterResponseData(organisationIdentification, this
                .getMessageType().name(), deviceIdentification, correlationUid, resultType, data);
        this.meterResponseDataService.enqueue(meterResponseData);
    }

    private void sendNotification(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final ResponseMessageResultType resultType) {
        final NotificationType notificationType = NotificationType.valueOf(this.getMessageType().name());
        this.notificationService.sendNotification(organisationIdentification, deviceIdentification, resultType.name(),
                correlationUid, this.getNotificationMessage(), notificationType);
    }

    private String getNotificationMessage() {
        return String.format("Response of type %s is available.", this.getMessageType().name());
    }
}
