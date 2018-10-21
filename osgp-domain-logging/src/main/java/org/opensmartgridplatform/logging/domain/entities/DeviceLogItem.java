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

    public DeviceLogItem(Builder builder) {
        this.incoming = builder.incoming;
        this.deviceUid = builder.deviceUid;
        this.encodedMessage = builder.encodedMessage;
        this.decodedMessage = builder.decodedMessage;
        this.deviceIdentification = builder.deviceIdentification;
        this.organisationIdentification = builder.organisationIdentification;
        this.valid = builder.valid;
        this.payloadMessageSerializedSize = builder.payloadMessageSerializedSize;
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

    public static class Builder {
        private boolean incoming;
        private String deviceUid;
        private String encodedMessage;
        private String decodedMessage;
        private String deviceIdentification;
        private String organisationIdentification;
        private boolean valid;
        private int payloadMessageSerializedSize;

        public Builder withIncoming(boolean incoming) {
            this.incoming = incoming;
            return this;
        }

        public Builder withDeviceUid(String deviceUid) {
            this.deviceUid = deviceUid;
            return this;
        }

        public Builder withEncodedMessage(String encodedMessage) {
            this.encodedMessage = encodedMessage;
            return this;
        }

        public Builder withDecodedMessage(String decodedMessage) {
            this.decodedMessage = decodedMessage;
            return this;
        }

        public Builder withDeviceIdentification(String deviceIdentification) {
            this.deviceIdentification = deviceIdentification;
            return this;
        }

        public Builder withOrganisationIdentification(String organisationIdentification) {
            this.organisationIdentification = organisationIdentification;
            return this;
        }

        public Builder withValid(boolean valid) {
            this.valid = valid;
            return this;
        }

        public Builder withPayloadMessageSerializedSize(int payloadMessageSerializedSize) {
            this.payloadMessageSerializedSize = payloadMessageSerializedSize;
            return this;
        }

        public DeviceLogItem build() {
            return new DeviceLogItem(this);
        }
    }
}
