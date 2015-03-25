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
