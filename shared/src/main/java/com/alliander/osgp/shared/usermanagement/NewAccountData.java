package com.alliander.osgp.shared.usermanagement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NewAccountData {

    private String organisationIdentification;
    private String username;
    private String newUsername;
    private String newPassword;
    private String newRole;
    private String newApplications;

    @JsonCreator
    public NewAccountData() { // TODO: check why this empty constructor is
        // needed

    }

    @JsonCreator
    public NewAccountData(@JsonProperty("organisationIdentification") final String organisationIdentification,
            @JsonProperty("username") final String username, @JsonProperty("newUsername") final String newUsername,
            @JsonProperty("newPassword") final String newPassword, @JsonProperty("newRole") final String newRole,
            @JsonProperty("newApplications") final String newApplications) {
        this.organisationIdentification = organisationIdentification;
        this.username = username;
        this.newUsername = newUsername;
        this.newPassword = newPassword;
        this.newRole = newRole;
        this.newApplications = newApplications;
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
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
