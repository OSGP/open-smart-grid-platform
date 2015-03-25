package com.alliander.osgp.shared.usermanagement;

public class ActivateOrganisationRequest {

    private String organisationIdentification;

    public ActivateOrganisationRequest() {

    }

    public ActivateOrganisationRequest(final String organisationIdentification) {
        this.organisationIdentification = organisationIdentification;
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }
}
