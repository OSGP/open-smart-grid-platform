/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.usermanagement;

import java.util.Date;

public class User {

    private String organisationIdentification;
    private String username;
    private String password;
    private String role;
    private String applications;
    private String firstName;
    private String middleName;
    private String lastName;
    private String emailAddress;
    private Date startDate;
    private Date expiryDateContract;
    private Date expiryDateBEIInstruction;;

    public User() {

    }

    public User(final AccountData accountData, final String firstName, final String middleName, final String lastName,
            final String emailAddress, final AccountDates accountDates) {
        this.organisationIdentification = accountData.getOrganisationIdentification();
        this.username = accountData.getUsername();
        this.password = accountData.getPassword();
        this.role = accountData.getRole();
        this.applications = accountData.getApplications();
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.startDate = accountDates.getStartDate();
        this.expiryDateContract = accountDates.getExpiryDateContract();
        this.expiryDateBEIInstruction = accountDates.getExpiryDateBEIInstruction();
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

    public String getFirstName() {
        return this.firstName;
    }

    public String getMiddleName() {
        return this.middleName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public Date getExpiryDateContract() {
        return this.expiryDateContract;
    }

    public Date getExpiryDateBEIInstruction() {
        return this.expiryDateBEIInstruction;
    }

}
