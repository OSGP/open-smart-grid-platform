package com.alliander.osgp.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class UnknownEntityException extends PlatformException {

    /**
     * 
     */
    private static final long serialVersionUID = 2360400169267313819L;
    private static final String MESSAGE = "%1$s with id \"%2$s\" could not be found.";

    public UnknownEntityException(final Class<?> entity, final String identification) {
        this(entity, identification, null);
    }

    public UnknownEntityException(final Class<?> entity, final String identification, final Exception innerException) {
        super(String.format(MESSAGE, entity.getSimpleName(), identification), innerException);
    }
}
