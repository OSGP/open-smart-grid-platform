package com.alliander.osgp.shared.infra.jms;

public class UnknownMessageTypeException extends Exception {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -8205251907838224642L;

    public UnknownMessageTypeException(final String message) {
        super(message);
    }

    public UnknownMessageTypeException(final String message, final Exception exception) {
        super(message, exception);
    }
}
