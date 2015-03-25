package com.alliander.osgp.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class UnregisteredDeviceException extends PlatformException {
    /**
     * 
     */
    private static final long serialVersionUID = -8628972116249878312L;
    private static final String MESSAGE = "Device %1$s is not registered";

    public UnregisteredDeviceException(final String deviceIdentification) {
        super(String.format(MESSAGE, deviceIdentification));
    }
}
