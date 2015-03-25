package com.alliander.osgp.shared.usermanagement;

public class OrganisationManagementClientException extends Exception {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -1123314377879408972L;

    public OrganisationManagementClientException(final String message) {
        super(message);
    }

    public OrganisationManagementClientException(final String message, final Throwable t) {
        super(message, t);
    }
}
