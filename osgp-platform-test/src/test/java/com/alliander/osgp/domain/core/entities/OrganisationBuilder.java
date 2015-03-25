package com.alliander.osgp.domain.core.entities;

import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;

public class OrganisationBuilder {
    private String organisationIdentification;
    private String name;
    private String prefix;

    private PlatformFunctionGroup functionGroup = PlatformFunctionGroup.USER;

    public OrganisationBuilder withOrganisationIdentification(final String organisationIdentification) {
        this.organisationIdentification = organisationIdentification;
        return this;
    }

    public OrganisationBuilder withName(final String name) {
        this.name = name;
        return this;
    }

    public OrganisationBuilder withPrefix(final String prefix) {
        this.prefix = prefix;
        return this;
    }

    public OrganisationBuilder withFunctionGroup(final PlatformFunctionGroup functionGroup) {
        this.functionGroup = functionGroup;
        return this;
    }

    public Organisation build() {
        return new Organisation(this.organisationIdentification, this.name, this.prefix, this.functionGroup);
    }
}
