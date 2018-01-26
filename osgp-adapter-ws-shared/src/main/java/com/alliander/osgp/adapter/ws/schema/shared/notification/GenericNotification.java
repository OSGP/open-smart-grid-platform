/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.schema.shared.notification;

public class GenericNotification {

    private String message;
    private String result;
    private String deviceIdentification;
    private String correlationUid;
    private String notificationType;

    /**
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * @return the result
     */
    public String getResult() {
        return this.result;
    }

    /**
     * @param result
     *            the result to set
     */
    public void setResult(final String result) {
        this.result = result;
    }

    /**
     * @return the deviceIdentification
     */
    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    /**
     * @param deviceIdentification
     *            the deviceIdentification to set
     */
    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    /**
     * @return the correlationUid
     */
    public String getCorrelationUid() {
        return this.correlationUid;
    }

    /**
     * @param correlationUid
     *            the correlationUid to set
     */
    public void setCorrelationUid(final String correlationUid) {
        this.correlationUid = correlationUid;
    }

    /**
     * @return the notificationType
     */
    public String getNotificationType() {
        return this.notificationType;
    }

    /**
     * @param notificationType
     *            the notificationType to set
     */
    public void setNotificationType(final String notificationType) {
        this.notificationType = notificationType;
    }

}
