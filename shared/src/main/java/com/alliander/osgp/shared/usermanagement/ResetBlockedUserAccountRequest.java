/**
 * Copyright 2015 Smart Society Services B.V.
 */
package com.alliander.osgp.shared.usermanagement;

public class ResetBlockedUserAccountRequest {

    private String username;

    public ResetBlockedUserAccountRequest() {
        // Empty constructor
    }

    public ResetBlockedUserAccountRequest(final String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }
}
