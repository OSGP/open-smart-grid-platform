/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.usermanagement;

import java.util.Date;

public class ChangeUser {

    private String organisationIdentification;
    private String username;
    private String newUsername;
    private String newPassword;
    private String newRole;
    private String newApplications;
    private String newFirstName;
    private String newMiddleName;
    private String newLastName;
    private String newEmailAddress;
    private Date startDate;
    private Date expiryDateBEIInstruction;

    public ChangeUser() {

    }

    public ChangeUser(final NewAccountData newAccountData, final String newFirstName, final String newMiddleName,
            final String newLastName, final String newEmailAddress, final AccountDates accountDates) {
        this.organisationIdentification = newAccountData.getOrganisationIdentification();
        this.username = newAccountData.getUsername();
        this.newUsername = newAccountData.getNewUsername();
        this.newPassword = newAccountData.getNewPassword();
        this.newRole = newAccountData.getNewRole();
        this.newApplications = newAccountData.getNewApplications();
        this.newFirstName = newFirstName;
        this.newMiddleName = newMiddleName;
        this.newLastName = newLastName;
        this.newEmailAddress = newEmailAddress;
        this.startDate = accountDates.getStartDate();
        this.expiryDateBEIInstruction = accountDates.getExpiryDateBEIInstruction();
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

    public Date getStartDate() {
        return this.startDate;
    }

    public Date getExpiryDateBEIInstruction() {
        return this.expiryDateBEIInstruction;
    }
}
