package com.alliander.osgp.shared.usermanagement;

public class ChangeOrganisationRequest {

    private String organisationIdentificationToChange;
    private String newOrganisationIdentification;
    private String newOrganisationName;
    private String functionGroup;

    public ChangeOrganisationRequest() {

    }

    public ChangeOrganisationRequest(final String organisationIdentificationToChange,
            final String newOrganisationIdentification, final String newOrganisationName, final String functionGroup) {
        this.organisationIdentificationToChange = organisationIdentificationToChange;
        this.newOrganisationIdentification = newOrganisationIdentification;
        this.newOrganisationName = newOrganisationName;
        this.functionGroup = functionGroup;
    }

    public String getOrganisationIdentificationToChange() {
        return this.organisationIdentificationToChange;
    }

    public String getNewOrganisationIdentification() {
        return this.newOrganisationIdentification;
    }

    public String getNewOrganisationName() {
        return this.newOrganisationName;
    }

    public String getFunctionGroup() {
        return this.functionGroup;
    }
}
