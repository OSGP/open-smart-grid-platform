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
    private String newFirstName;
    private String newMiddleName;
    private String newLastName;
    private String newEmailAddress;
    private String newPassword;
    private String newRole;
    private String newApplications;
    private Date newStartDate;
    private Date newExpiryDateBEIInstruction;

    public ChangeUserRequest() {

    }

    public ChangeUserRequest(final ChangeUser changeUser) {
        this.organisationIdentificationForUser = changeUser.getOrganisationIdentification();
        this.username = changeUser.getUsername();
        this.newFirstName = changeUser.getNewFirstName();
        this.newMiddleName = changeUser.getNewMiddleName();
        this.newLastName = changeUser.getNewLastName();
        this.newEmailAddress = changeUser.getNewEmailAddress();
        this.newUsername = changeUser.getNewUsername();
        this.newPassword = changeUser.getNewPassword();
        this.newRole = changeUser.getNewRole();
        this.newApplications = changeUser.getNewApplications();
        this.newStartDate = changeUser.getStartDate();
        this.newExpiryDateBEIInstruction = changeUser.getExpiryDateBEIInstruction();
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

    public String getNewFirstName() {
        return this.newFirstName;
    }

    public String getNewMiddleName() {
        return this.newMiddleName;
    }

    public String getNewLastName() {
        return this.newLastName;
    }

    public String getNewEmailAddress() {
        return this.newEmailAddress;
    }

    public Date getNewStartDate() {
        return this.newStartDate;
    }

    public Date getNewExpiryDateBEIInstruction() {
        return this.newExpiryDateBEIInstruction;
    }
}
