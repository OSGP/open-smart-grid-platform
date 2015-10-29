package com.alliander.osgp.shared.usermanagement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountData {

    private String organisationIdentification;
    private String username;
    private String password;
    private String role;
    private String applications;

    @JsonCreator
    public AccountData() { // TODO: check why this empty constructor is needed

    }

    @JsonCreator
    public AccountData(@JsonProperty("organisationIdentification") final String organisationIdentification,
            @JsonProperty("username") final String username, @JsonProperty("password") final String password,
            @JsonProperty("role") final String role, @JsonProperty("applications") final String applications) {
        this.organisationIdentification = organisationIdentification;
        this.username = username;
        this.password = password;
        this.role = role;
        this.applications = applications;
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

    public String getRole() {
        return this.role;
    }

    public String getApplications() {
        return this.applications;
    }
}
