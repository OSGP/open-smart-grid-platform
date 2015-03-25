package com.alliander.osgp.domain.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.alliander.osgp.shared.domain.entities.AbstractEntity;

@Entity
public class OslpLogItem extends AbstractEntity {
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
    private OslpLogItem() {
        // Empty constructor for Hibernate.
    }

    public OslpLogItem(final String organisationIdentification, final String deviceUid,
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
