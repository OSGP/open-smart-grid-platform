package com.alliander.osgp.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class ArgumentNullOrEmptyException extends PlatformException {

    /**
     * 
     */
    private static final long serialVersionUID = -2507623526972653558L;
    private static final String MESSAGE = "Argument [%1$s] is null or empty";

    public ArgumentNullOrEmptyException(final String argument) {
        super(String.format(MESSAGE, argument));
    }
}
