/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.elster.device;

public class DeviceRequest {

    private String organisationIdentification;
    private String deviceIdentification;
    private String correlationUid;
    private String domain = null;
    private String domainVersion = null;
    private String messageType = null;
    private String ipAddress = null;
    private int retryCount = 0;
    private boolean isScheduled = false;

    public DeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid) {
        this.organisationIdentification = organisationIdentification;
        this.deviceIdentification = deviceIdentification;
        this.correlationUid = correlationUid;
    }

    public DeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final String domain, final String domainVersion, final String messageType,
            final String ipAddress, final int retryCount, final boolean isScheduled) {
        this.organisationIdentification = organisationIdentification;
        this.deviceIdentification = deviceIdentification;
        this.correlationUid = correlationUid;
        this.domain = domain;
        this.domainVersion = domainVersion;
        this.messageType = messageType;
        this.ipAddress = ipAddress;
        this.retryCount = retryCount;
        this.isScheduled = isScheduled;
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

    public String getDomain() {
        return this.domain;
    }

    public String getDomainVersion() {
        return this.domainVersion;
    }

    public String getMessageType() {
        return this.messageType;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public int getRetryCount() {
        return this.retryCount;
    }

    public boolean isScheduled() {
        return this.isScheduled;
    }
}
