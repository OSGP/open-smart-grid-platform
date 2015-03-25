package com.alliander.osgp.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class ConfigurationException extends PlatformException {

    /**
     * 
     */
    private static final long serialVersionUID = 4535607746025926682L;
    private static final String MESSAGE = "Platform configuration is incorrect or incomplete.";

    public ConfigurationException() {
        super(MESSAGE);
    }
}
