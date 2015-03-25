package com.alliander.osgp.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class OsgpCoreException extends Exception {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 2714612190124325351L;

    public OsgpCoreException(final String message) {
        super(message);
    }

    public OsgpCoreException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
