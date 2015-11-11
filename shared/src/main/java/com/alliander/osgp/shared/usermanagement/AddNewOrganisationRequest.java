/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.usermanagement;

import java.util.Date;

public class AddNewOrganisationRequest {

    private String organisationIdentification;
    private String organisationName;
    private String organisationPrefix;
    private String functionGroup;
    private boolean enabled;
    private Date expiryDateContract;
    private String emailAddress;
    private String phoneNumber;

    public AddNewOrganisationRequest() {

    }

    public AddNewOrganisationRequest(final String organisationIdentification, final String organisationName,
            final String organisationPrefix, final Credentials credentials) {
        this.organisationIdentification = organisationIdentification;
        this.organisationName = organisationName;
        this.organisationPrefix = organisationPrefix;
        this.functionGroup = credentials.getFunctionGroup();
        this.enabled = credentials.isEnabled();
        this.expiryDateContract = credentials.getExpiryDateContract();
        this.emailAddress = credentials.getEmailAddress();
        this.phoneNumber = credentials.getPhoneNumber();
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }

    public String getOrganisationName() {
        return this.organisationName;
    }

    public String getOrganisationPrefix() {
        return this.organisationPrefix;
    }

    public String getFunctionGroup() {
        return this.functionGroup;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public Date getExpiryDateContract() {
        return this.expiryDateContract;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }
}
