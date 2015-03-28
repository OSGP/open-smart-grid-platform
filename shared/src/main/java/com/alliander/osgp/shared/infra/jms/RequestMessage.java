package com.alliander.osgp.shared.infra.jms;

import java.io.Serializable;

public class RequestMessage implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -9061131896296150992L;

    protected String correlationUid;
    protected String organisationIdentification;
    protected String deviceIdentification;
    protected Serializable request;

    public RequestMessage(final String correlationUid, final String organisationIdentification,
            final String deviceIdentification, final Serializable request) {
        this.correlationUid = correlationUid;
        this.organisationIdentification = organisationIdentification;
        this.deviceIdentification = deviceIdentification;
        this.request = request;
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

    public Serializable getRequest() {
        return this.request;
    }
}
