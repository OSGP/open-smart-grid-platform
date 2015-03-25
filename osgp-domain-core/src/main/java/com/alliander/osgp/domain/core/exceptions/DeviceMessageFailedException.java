package com.alliander.osgp.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class DeviceMessageFailedException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 2589780904277543380L;
    private static final String MESSAGE = "Device Message Failed";

    public DeviceMessageFailedException() {
        super(MESSAGE);
    }
}
