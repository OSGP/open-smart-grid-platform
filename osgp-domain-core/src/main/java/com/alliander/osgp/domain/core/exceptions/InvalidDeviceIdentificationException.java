package com.alliander.osgp.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class InvalidDeviceIdentificationException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 4766085145882763249L;
    private static final String message = "Invalid Device Identification";

    public InvalidDeviceIdentificationException() {
        super(message);
    }
}
