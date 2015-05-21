/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.exceptions;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

import com.alliander.osgp.domain.core.entities.Organisation;

@SoapFault(faultCode = FaultCode.SERVER)
public class InvalidOrganisationException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 2734765757362133547L;
    private final static String message = "Invalid organisation [%1$s], validation errors: %2$s";

    public InvalidOrganisationException(final Organisation organisation,
            final Set<ConstraintViolation<Organisation>> constraintViolations) {
        super(String.format(message, organisation, convertToString(constraintViolations)));
    }

    private static String convertToString(final Set<ConstraintViolation<Organisation>> constraintViolations) {
        final StringBuilder violations = new StringBuilder();

        for (final ConstraintViolation<Organisation> violation : constraintViolations) {
            violations.append(violation.getMessage() + "\n");
        }

        return violations.toString();
    }
}
