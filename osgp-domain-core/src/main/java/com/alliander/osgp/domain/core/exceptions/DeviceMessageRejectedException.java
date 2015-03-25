package com.alliander.osgp.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class DeviceMessageRejectedException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -5494355964203067084L;
    private static final String MESSAGE = "Device Message Rejected";

    public DeviceMessageRejectedException() {
        super(MESSAGE);
    }
}
