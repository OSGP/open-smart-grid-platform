package com.alliander.osgp.adapter.protocol.oslp.exceptions;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.StringUtils;

public class ValidationException extends ProtocolAdapterException {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -8142436786681141317L;

    private static final String DEFAULT_MESSAGE = "Validation Exception";

    private Set<? extends ConstraintViolation<?>> constraintViolations;

    public ValidationException() {
        super(DEFAULT_MESSAGE);
    }

    public ValidationException(final String message) {
        super(message);
    }

    public ValidationException(final Set<? extends ConstraintViolation<?>> constraintViolations) {
        super(DEFAULT_MESSAGE + ", violations: " + convertToString(constraintViolations));
        this.constraintViolations = constraintViolations;
    }

    public ValidationException(final String message, final Set<? extends ConstraintViolation<?>> constraintViolations) {
        super(message);
        this.constraintViolations = constraintViolations;
    }

    public Set<? extends ConstraintViolation<?>> getConstraintViolations() {
        return this.constraintViolations;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();

        result.append(DEFAULT_MESSAGE);

        if (this.constraintViolations != null && !this.constraintViolations.isEmpty()) {
            result.append(", violations: ");
            for (final ConstraintViolation<?> violation : this.constraintViolations) {
                result.append(violation.getPropertyPath() + ", invalid value: " + violation.getInvalidValue() + "; ");
            }
        }

        return result.toString();
    }

    private static String convertToString(final Set<? extends ConstraintViolation<?>> constraintViolations) {
        final StringBuilder violations = new StringBuilder();

        for (final ConstraintViolation<?> violation : constraintViolations) {

            if (!StringUtils.isBlank(violation.getMessage())) {
                violations.append(violation.getMessage());
            } else {
                violations.append(violation.getPropertyPath());
            }

            violations.append("; ");
        }

        return violations.toString();
    }
}
