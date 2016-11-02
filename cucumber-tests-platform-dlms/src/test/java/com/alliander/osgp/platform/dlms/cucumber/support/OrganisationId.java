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
