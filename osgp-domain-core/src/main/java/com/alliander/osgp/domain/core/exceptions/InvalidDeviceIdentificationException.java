/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class InvalidDeviceIdentificationException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 4766085145882763249L;
    private static final String message = "Invalid Device Identification";

    public InvalidDeviceIdentificationException() {
        super(message);
    }
}
