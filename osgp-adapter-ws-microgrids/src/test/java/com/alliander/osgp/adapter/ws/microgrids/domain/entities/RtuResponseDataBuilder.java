package com.alliander.osgp.adapter.ws.microgrids.domain.entities;

import java.io.Serializable;
import java.util.Date;

import org.joda.time.DateTime;

import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

public class RtuResponseDataBuilder {

    private Date creationTime = DateTime.now().toDate();
    private String organisationIdentification = "test-org";
    private String deviceIdentification = "test-rtu";
    private String correlationUid = "test-org|||test-rtu|||20170101000000000";
    private String messageType = "GET_DATA";
    private Serializable messageData = null;
    private ResponseMessageResultType resultType = ResponseMessageResultType.OK;

    // TODO OC-31 - Find a better way to update creation time for Rtu Response
    // Data without making it publicly available
    public RtuResponseData updateCreationTime(final RtuResponseData response, final Date creationTime) {
        response.updateCreationTime(creationTime);
        return response;
    }

    public RtuResponseData build() {
        final RtuResponseData rtuResponseData = new RtuResponseData(this.organisationIdentification, this.messageType,
                this.deviceIdentification, this.correlationUid, this.resultType, this.messageData);
        rtuResponseData.updateCreationTime(this.creationTime);
        return rtuResponseData;
    }

    public RtuResponseDataBuilder withCreationTime(final Date creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public RtuResponseDataBuilder withOrganisationIdentification(final String organisationIdentification) {
        this.organisationIdentification = organisationIdentification;
        return this;
    }

    public RtuResponseDataBuilder withDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
        return this;
    }

    public RtuResponseDataBuilder withCorrelationUid(final String correlationUid) {
        this.correlationUid = correlationUid;
        return this;
    }

    public RtuResponseDataBuilder withMessageType(final String messageType) {
        this.messageType = messageType;
        return this;
    }

    public RtuResponseDataBuilder withMessageData(final Serializable messageData) {
        this.messageData = messageData;
        return this;
    }

    public RtuResponseDataBuilder withResultType(final ResponseMessageResultType resultType) {
        this.resultType = resultType;
        return this;
    }
}
