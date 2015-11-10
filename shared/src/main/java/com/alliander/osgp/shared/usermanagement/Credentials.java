package com.alliander.osgp.shared.usermanagement;

import java.util.Date;

public class Credentials {
    private final String functionGroup;
    private final Date expiryDateContract;
    private final boolean enabled;

    public Credentials(final String functionGroup, final Date expiryDateContract, final boolean enabled) {
        super();
        this.functionGroup = functionGroup;
        this.expiryDateContract = expiryDateContract;
        this.enabled = enabled;
    }

    public String getFunctionGroup() {
        return this.functionGroup;
    }

    public Date getExpiryDateContract() {
        return this.expiryDateContract;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}
