package com.alliander.osgp.shared.usermanagement;

public class AddNewOrganisationRequest {

    private String organisationIdentification;
    private String organisationName;
    private String organisationPrefix;
    private String functionGroup;
    private boolean enabled;

    public AddNewOrganisationRequest() {

    }

    public AddNewOrganisationRequest(final String organisationIdentification, final String organisationName,
            final String organisationPrefix, final String functionGroup, final boolean enabled) {
        this.organisationIdentification = organisationIdentification;
        this.organisationName = organisationName;
        this.organisationPrefix = organisationPrefix;
        this.functionGroup = functionGroup;
        this.enabled = enabled;
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }

    public String getOrganisationName() {
        return this.organisationName;
    }

    public String getOrganisationPrefix() {
        return this.organisationPrefix;
    }

    public String getFunctionGroup() {
        return this.functionGroup;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}
