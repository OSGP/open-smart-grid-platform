package com.alliander.osgp.shared.usermanagement;

public class AuthenticationClientException extends Exception {

    /**
     * Serial Verion UID.
     */
    private static final long serialVersionUID = 6002355875672167985L;

    public AuthenticationClientException(final String message) {
        super(message);
    }

    public AuthenticationClientException(final String message, final Throwable t) {
        super(message, t);
    }
}
