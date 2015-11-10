package com.alliander.osgp.shared.usermanagement;

import java.util.Date;

public class AccountDates {

    private Date startDate;
    private Date expiryDateBEIInstruction;

    public AccountDates(final Date startDate, final Date expiryDateBEIInstruction) {
        this.startDate = startDate;
        this.expiryDateBEIInstruction = expiryDateBEIInstruction;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public Date getExpiryDateBEIInstruction() {
        return this.expiryDateBEIInstruction;
    }
}
