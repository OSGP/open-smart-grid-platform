package com.alliander.osgp.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class EmptyUserNameSoapHeaderException extends PlatformException {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -1189166045760569232L;
    private static final String MESSAGE = "UserName Soap Header is empty or missing";

    public EmptyUserNameSoapHeaderException() {
        super(MESSAGE);
    }
}
