package com.alliander.osgp.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class NotAuthorizedException extends PlatformException {

    /**
     * 
     */
    private static final long serialVersionUID = 2343397355361259276L;
    private static final String MESSAGE = "Organisation [%1$s] is not authorized for action";

    public NotAuthorizedException(final String organisationIdentification) {
        super(String.format(MESSAGE, organisationIdentification));
    }
}
