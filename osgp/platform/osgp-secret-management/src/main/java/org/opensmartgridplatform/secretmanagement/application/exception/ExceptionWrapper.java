package org.opensmartgridplatform.secretmanagement.application.exception;

/**
 * Unchecked exception that wraps another (checked) exception.
 * Can be used to handle checked exception in streams.
 */
public class ExceptionWrapper extends RuntimeException {
    private static final long serialVersionUID = -1239332310446200862L;

    public ExceptionWrapper(Exception exc) {
        super(exc);
    }
}
