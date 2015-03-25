package com.alliander.osgp.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class PlatformException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 2879663396838174171L;

    public PlatformException(final String message) {
        super(message);
    }

    public PlatformException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
