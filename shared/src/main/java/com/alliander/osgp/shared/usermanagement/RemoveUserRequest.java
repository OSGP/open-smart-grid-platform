package com.alliander.osgp.shared.usermanagement;

public class RemoveUserRequest {

    private String organisationIdentification;
    private String username;

    public RemoveUserRequest() {

    }

    public RemoveUserRequest(final String organisationIdentification, final String username) {
        this.organisationIdentification = organisationIdentification;
        this.username = username;
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }

    public String getUsername() {
        return this.username;
    }
}
