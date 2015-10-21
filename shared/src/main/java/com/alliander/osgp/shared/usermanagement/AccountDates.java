package com.alliander.osgp.shared.usermanagement;

import java.util.Date;

public class AccountDates {

    private Date startDate;
    private Date expiryDateContract;
    private Date expiryDateBEIInstruction;

    public AccountDates(final Date startDate, final Date expiryDateContract, final Date expiryDateBEIInstruction) {
        this.startDate = startDate;
        this.expiryDateContract = expiryDateContract;
        this.expiryDateBEIInstruction = expiryDateBEIInstruction;
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
