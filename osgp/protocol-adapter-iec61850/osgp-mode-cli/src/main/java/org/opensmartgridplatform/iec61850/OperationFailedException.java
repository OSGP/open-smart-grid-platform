package org.opensmartgridplatform.iec61850;

public class OperationFailedException extends Exception {

    private static final long serialVersionUID = 1L;

    public OperationFailedException(final String message, final Throwable inner) {
        super(message, inner);
    }

}
