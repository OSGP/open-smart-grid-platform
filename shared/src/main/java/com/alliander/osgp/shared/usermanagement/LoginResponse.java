/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.usermanagement;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse extends ApiResponse {

    private String organisationIdentification;
    private String role;
    private List<String> applications;
    private String token;
    private List<PlatformDomain> domains;

    @JsonCreator
    public LoginResponse() {

    }

    @JsonCreator
    public LoginResponse(@JsonProperty("organisationIdentification") final String organisationIdentification,
            @JsonProperty("role") final String role, @JsonProperty("applications") final List<String> applications,
            @JsonProperty("token") final String token, @JsonProperty("domains") final List<PlatformDomain> domains) {
        this.organisationIdentification = organisationIdentification;
        this.role = role;
        this.applications = applications;
        this.token = token;
        this.setDomains(domains);
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }

    public String getRole() {
        return this.role;
    }

    public List<String> getApplications() {
        return this.applications;
    }

    public String getToken() {
        return this.token;
    }

    public List<PlatformDomain> getDomains() {
        return this.domains;
    }

    public void setDomains(final List<PlatformDomain> domains) {
        this.domains = domains;
    }
}
