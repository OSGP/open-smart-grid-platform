package com.alliander.osgp.platform.cucumber.smartmeteringmonitoring;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.support.OrganisationId;

import cucumber.api.java.en.And;

public class Organisation {
    @Autowired
    private OrganisationId organisationId;

    @And("^an organisation with OrganisationID \"([^\"]*)\"$")
    public void anOrganisationWithOrganisationID(final String organisationId) throws Throwable {
        this.organisationId.setOrganisationId(organisationId);
    }
}
