/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.microgrids.domain.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.alliander.osgp.shared.domain.entities.AbstractEntity;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Entity
@Table(name = "rtu_response_data")
public class RtuResponseData extends AbstractEntity {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 493031146792643786L;

    @Column(length = 255)
    private String organisationIdentification;

    @Column(length = 255)
    private String messageType;

    @Column(length = 255)
    private String deviceIdentification;

    @Column(length = 255)
    private String correlationUid;

    @Enumerated(EnumType.STRING)
    private ResponseMessageResultType resultType;

    @Type(type = "java.io.Serializable")
    private Serializable messageData;

    @SuppressWarnings("unused")
    private RtuResponseData() {

    }

    public RtuResponseData(final String organisationIdentification, final String messageType,
            final String deviceIdentification, final String correlationUid, final ResponseMessageResultType resultType,
            final Serializable messageData) {
        this.organisationIdentification = organisationIdentification;
        this.messageType = messageType;
        this.deviceIdentification = deviceIdentification;
        this.correlationUid = correlationUid;
        this.resultType = resultType;
        this.messageData = messageData;
    }

    public String getCorrelationUid() {
        return this.correlationUid;
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public String getMessageType() {
        return this.messageType;
    }

    public Serializable getMessageData() {
        return this.messageData;
    }

    public ResponseMessageResultType getResultType() {
        return this.resultType;
    }
}
