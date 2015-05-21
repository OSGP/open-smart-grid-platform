/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
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
