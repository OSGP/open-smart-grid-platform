/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.usermanagement;

public class AddNewUserRequest {

    private String organisationIdentification;
    private String username;
    private String password;
    private String role;
    private String applications;

    public AddNewUserRequest() {

    }

    public AddNewUserRequest(final String organisationIdentification, final String username, final String password,
            final String role, final String applications) {
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
