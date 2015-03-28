package com.alliander.osgp.shared.usermanagement;

public class ChangeUserRequest {

    private String organisationIdentificationForUser;
    private String username;
    private String newUsername;
    private String newPassword;
    private String newRole;
    private String newApplications;

    public ChangeUserRequest() {

    }

    public ChangeUserRequest(final String organisationIdentificationForUser, final String username,
            final String newUsername, final String newPassword, final String newRole, final String newApplications) {
        this.organisationIdentificationForUser = organisationIdentificationForUser;
        this.username = username;
        this.newUsername = newUsername;
        this.newPassword = newPassword;
        this.newRole = newRole;
        this.newApplications = newApplications;
    }

    public String getOrganisationIdentificationForUser() {
        return this.organisationIdentificationForUser;
    }

    public String getUsername() {
        return this.username;
    }

    public String getNewUsername() {
        return this.newUsername;
    }

    public String getNewPassword() {
        return this.newPassword;
    }

    public String getNewRole() {
        return this.newRole;
    }

    public String getNewApplications() {
        return this.newApplications;
    }
}
