package org.osgp.adapter.protocol.dlms.infra.messaging;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import com.alliander.osgp.shared.infra.jms.Constants;

/**
 * this class is a data container which contains attributes that can be set from
 * an ObjectMessage. It helps preventing duplicate code blocks in the messaging
 * processors
 *
 */
public class DlmsDeviceMessageMetadata {
    private String correlationUid;
    private String domain;
    private String domainVersion;
    private String messageType;
    private String organisationIdentification;
    private String deviceIdentification;
    private String ipAddress;

    @Override
    public String toString() {
        return String
                .format("DlmsDeviceMessageMetadata[correlationUid=%s, domain=%s, domainVersion=%s, messageType=%s, organisation=%s, device=%s, ipAddress=%s]",
                        this.correlationUid, this.domain, this.domainVersion, this.messageType,
                        this.organisationIdentification, this.deviceIdentification, this.ipAddress);
    }

    /**
     * By using the ObjectMessage, the attributes of the data container are set
     *
     * @param message
     * @throws JMSException
     */
    public void handleMessage(final ObjectMessage message) throws JMSException {
        this.correlationUid = message.getJMSCorrelationID();
        this.domain = message.getStringProperty(Constants.DOMAIN);
        this.domainVersion = message.getStringProperty(Constants.DOMAIN_VERSION);
        this.messageType = message.getJMSType();
        this.organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
        this.deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
        this.ipAddress = message.getStringProperty(Constants.IP_ADDRESS);
    }

    public String getCorrelationUid() {
        return this.correlationUid;
    }

    public void setCorrelationUid(final String correlationUid) {
        this.correlationUid = correlationUid;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(final String domain) {
        this.domain = domain;
    }

    public String getDomainVersion() {
        return this.domainVersion;
    }

    public void setDomainVersion(final String domainVersion) {
        this.domainVersion = domainVersion;
    }

    public String getMessageType() {
        return this.messageType;
    }

    public void setMessageType(final String messageType) {
        this.messageType = messageType;
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }

    public void setOrganisationIdentification(final String organisationIdentification) {
        this.organisationIdentification = organisationIdentification;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
