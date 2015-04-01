package com.alliander.osgp.shared.infra.jms;

import java.io.Serializable;

import com.alliander.osgp.shared.exceptionhandling.OsgpException;

public class ResponseMessage implements java.io.Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -214808702310700742L;

    private final String correlationUid;
    private final String organisationIdentification;
    private final String deviceIdentification;
    private final ResponseMessageResultType result;
    private final OsgpException osgpException;
    private final Object dataObject;

    public ResponseMessage(final String correlationUid, final String organisationIdentification,
            final String deviceIdentification, final ResponseMessageResultType result, final OsgpException osgpException,
            final Object dataObject) {
        this.correlationUid = correlationUid;
        this.organisationIdentification = organisationIdentification;
        this.deviceIdentification = deviceIdentification;
        this.result = result;
        this.osgpException = osgpException;
        this.dataObject = dataObject;
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

    public ResponseMessageResultType getResult() {
        return this.result;
    }

    public OsgpException getOsgpException() {
        return this.osgpException;
    }

    public Object getDataObject() {
        return this.dataObject;
    }
}
