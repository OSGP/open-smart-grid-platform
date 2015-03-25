package com.alliander.osgp.shared.infra.jms;

public class ResponseMessage implements java.io.Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -214808702310700742L;

    private final String correlationUid;
    private final String organisationIdentification;
    private final String deviceIdentification;
    private final ResponseMessageResultType result;
    private final String description;
    private final Object dataObject;

    public ResponseMessage(final String correlationUid, final String organisationIdentification,
            final String deviceIdentification, final ResponseMessageResultType result, final String description,
            final Object dataObject) {
        this.correlationUid = correlationUid;
        this.organisationIdentification = organisationIdentification;
        this.deviceIdentification = deviceIdentification;
        this.result = result;
        this.description = description;
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

    public String getDescription() {
        return this.description;
    }

    public Object getDataObject() {
        return this.dataObject;
    }
}
