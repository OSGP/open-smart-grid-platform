package com.alliander.osgp.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class UnknownMessageException extends PlatformException {

    /**
     * 
     */
    private static final long serialVersionUID = -5443719028089122868L;
    private final static String DEFAULT_MESSAGE = "Unknown message Exception";

    public UnknownMessageException() {
        super(DEFAULT_MESSAGE);
    }

    public UnknownMessageException(final String message) {
        super(message);
    }

    public UnknownMessageException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
