package com.alliander.osgp.shared.usermanagement;

public class ResponseException extends Exception {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -6152977924946851822L;

    public ResponseException(final String message) {
        super(message);
    }

    public ResponseException(final String message, final Throwable t) {
        super(message, t);
    }
}
