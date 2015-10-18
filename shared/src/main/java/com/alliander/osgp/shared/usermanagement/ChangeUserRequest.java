/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.usermanagement;

import java.util.Date;

public class ChangeUserRequest {

    private String organisationIdentificationForUser;
    private String username;
    private String newUsername;
    private String newFirstname;
    private String newMiddlename;
    private String newLastname;
    private String newEmailAddress;
    private String newPassword;
    private String newRole;
    private String newApplications;
    private Date newStartDate;
    private Date newExpiryDateContract;
    private Date newExpiryDateBEIInstruction;

    public ChangeUserRequest() {

    }

    public ChangeUserRequest(final String organisationIdentificationForUser, final String username,
            final String newUsername, final String newFirstname, final String newMiddlename, final String newLastname,
            final String newEmailAddress, final String newPassword, final String newRole, final String newApplications,
            final Date newStartDate, final Date newExpiryDateContract, final Date newExpiryDateBEIInstruction) {
        this.organisationIdentificationForUser = organisationIdentificationForUser;
        this.username = username;
        this.newFirstname = newFirstname;
        this.newMiddlename = newMiddlename;
        this.newLastname = newLastname;
        this.newEmailAddress = newEmailAddress;
        this.newUsername = newUsername;
        this.newPassword = newPassword;
        this.newRole = newRole;
        this.newApplications = newApplications;
        this.newStartDate = newStartDate;
        this.newExpiryDateContract = newExpiryDateContract;
        this.newExpiryDateBEIInstruction = newExpiryDateBEIInstruction;
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

    public String getNewFirstname() {
        return this.newFirstname;
    }

    public String getNewMiddlename() {
        return this.newMiddlename;
    }

    public String getNewLastname() {
        return this.newLastname;
    }

    public String getNewEmailAddress() {
        return this.newEmailAddress;
    }

    public Date getNewStartDate() {
        return this.newStartDate;
    }

    public Date getNewExpiryDateContract() {
        return this.newExpiryDateContract;
    }

    public Date getNewExpiryDateBEIInstruction() {
        return this.newExpiryDateBEIInstruction;
    }
}
