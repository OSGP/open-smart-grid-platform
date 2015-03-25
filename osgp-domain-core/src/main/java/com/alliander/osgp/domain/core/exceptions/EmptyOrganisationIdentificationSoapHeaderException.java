package com.alliander.osgp.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class EmptyOrganisationIdentificationSoapHeaderException extends PlatformException {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 3236913216772885583L;
    private static final String MESSAGE = "OrganisationIdentification Soap Header is empty or missing";

    public EmptyOrganisationIdentificationSoapHeaderException() {
        super(MESSAGE);
    }
}
