/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
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
