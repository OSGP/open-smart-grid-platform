package com.alliander.osgp.shared.usermanagement;

public class ChangeUserPasswordRequest {

    private String organisationIdentification;
    private String username;
    private String password;

    public ChangeUserPasswordRequest() {

    }

    public ChangeUserPasswordRequest(final String organisationIdentification, final String username,
            final String password) {
        this.organisationIdentification = organisationIdentification;
        this.username = username;
        this.password = password;
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}
