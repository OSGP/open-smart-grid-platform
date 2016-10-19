package org.osgp.adapter.protocol.dlms.exceptions;

public class FirmwareImageFactoryException extends Exception {

    private static final long serialVersionUID = 1L;

    public FirmwareImageFactoryException() {
        super();
    }

    public FirmwareImageFactoryException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public FirmwareImageFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public FirmwareImageFactoryException(String message) {
        super(message);
    }

    public FirmwareImageFactoryException(Throwable cause) {
        super(cause);
    }
}
