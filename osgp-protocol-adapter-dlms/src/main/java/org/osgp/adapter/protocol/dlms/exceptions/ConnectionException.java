package org.osgp.adapter.protocol.dlms.exceptions;

public class ConnectionException extends RuntimeException {
    private static final long serialVersionUID = -4527258679522467801L;

    public ConnectionException() {
        super();
    }

    public ConnectionException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ConnectionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ConnectionException(final String message) {
        super(message);
    }

    public ConnectionException(final Throwable cause) {
        super(cause);
    }
}
