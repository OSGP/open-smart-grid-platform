package com.alliander.osgp.domain.core.exceptions;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

import com.alliander.osgp.domain.core.valueobjects.Schedule;

@SoapFault(faultCode = FaultCode.SERVER)
public class InvalidScheduleException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 3700196675363961125L;
    private final static String message = "Invalid schedule, validation errors: %1$s";

    public InvalidScheduleException(final Set<ConstraintViolation<Schedule>> constraintViolations) {
        super(String.format(message, convertToString(constraintViolations)));
    }

    private static String convertToString(final Set<ConstraintViolation<Schedule>> constraintViolations) {
        final StringBuilder violations = new StringBuilder();

        for (final ConstraintViolation<Schedule> violation : constraintViolations) {
            violations.append(violation.getPropertyPath() + " " + violation.getMessage() + "; ");
        }

        return violations.toString();
    }
}
