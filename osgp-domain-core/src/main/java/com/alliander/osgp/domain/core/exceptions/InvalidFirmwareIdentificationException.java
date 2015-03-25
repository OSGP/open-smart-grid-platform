package com.alliander.osgp.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class InvalidFirmwareIdentificationException extends PlatformException {

    /**
     * 
     */
    private static final long serialVersionUID = -2033954329227850371L;
    private static final String MESSAGE_FORMAT = "Invalid firmware identification: [%s]";

    public InvalidFirmwareIdentificationException(final String identification) {
        super(String.format(MESSAGE_FORMAT, identification));
    }
}
