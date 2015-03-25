package com.alliander.osgp.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class CertificateInvalidException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 5979052338314893813L;
    private static final String MESSAGE = "Client certificate is untrusted or invalid for organisation [%1$s]";

    public CertificateInvalidException(final String organisationIdentification) {
        super(String.format(MESSAGE, organisationIdentification));
    }
}
