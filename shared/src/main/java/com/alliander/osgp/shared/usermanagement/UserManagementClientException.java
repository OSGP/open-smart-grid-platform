package com.alliander.osgp.shared.usermanagement;

public class UserManagementClientException extends Exception {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 4869922322193170605L;

    public UserManagementClientException(final String message) {
        super(message);
    }

    public UserManagementClientException(final String message, final Throwable t) {
        super(message, t);
    }
}
