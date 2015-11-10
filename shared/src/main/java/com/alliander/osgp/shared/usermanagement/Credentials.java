/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
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
