package com.alliander.osgp.shared.usermanagement;

public class RemoveOrganisationRequest {

    private String organisationIdentification;

    public RemoveOrganisationRequest() {

    }

    public RemoveOrganisationRequest(final String organisationIdentification) {
        this.organisationIdentification = organisationIdentification;
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }
}
