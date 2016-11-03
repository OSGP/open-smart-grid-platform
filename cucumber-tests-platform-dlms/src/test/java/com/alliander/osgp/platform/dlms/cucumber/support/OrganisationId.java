/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.support;

import org.springframework.stereotype.Component;

@Component
public class OrganisationId {
    private String organisationId;

    public void setOrganisationId(final String organisationId) {
        this.organisationId = organisationId;
    }

    public String getOrganisationId() {
        return this.organisationId;
    }
}
