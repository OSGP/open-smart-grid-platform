package com.alliander.osgp.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class EmptyApplicationNameSoapHeaderException extends PlatformException {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 8458784656352710077L;
    private static final String MESSAGE = "ApplicationName Soap Header is empty or missing";

    public EmptyApplicationNameSoapHeaderException() {
        super(MESSAGE);
    }
}
