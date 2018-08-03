/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.logging.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
public class DeviceLogItem extends AbstractEntity {
    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -60074765997151584L;

    private static final int MAX_MESSAGE_LENGTH = 8000;

    private boolean incoming;

    private String deviceUid;

    @Column(length = MAX_MESSAGE_LENGTH)
    private String encodedMessage;

    @Column(length = MAX_MESSAGE_LENGTH)
    private String decodedMessage;

    private String deviceIdentification;

    private String organisationIdentification;

    private boolean valid;

    @Column(name = "data_size")
    private int payloadMessageSerializedSize;

    @SuppressWarnings("unused")
    private DeviceLogItem() {
        // Empty constructor for Hibernate.
    }

    public DeviceLogItem(final String organisationIdentification, final String deviceUid,
            final String deviceIdentification, final boolean incoming, final boolean valid,
            final String encodedMessage, final String decodedMessage, final int payloadMessageSerializedSize) {
        this.organisationIdentification = organisationIdentification;
        this.deviceUid = deviceUid;
        this.deviceIdentification = deviceIdentification;
        this.incoming = incoming;
        this.valid = valid;
        this.encodedMessage = encodedMessage;
        this.decodedMessage = decodedMessage;
        this.payloadMessageSerializedSize = payloadMessageSerializedSize;
    }

    public boolean isIncoming() {
        return this.incoming;
    }

    public String getDeviceUid() {
        return this.deviceUid;
    }

    public String getEncodedMessage() {
        return this.encodedMessage;
    }

    public String getDecodedMessage() {
        return this.decodedMessage;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }

    public boolean isValid() {
        return this.valid;
    }

    public int getPayloadMessageSerializedSize() {
        return this.payloadMessageSerializedSize;
    }
}
