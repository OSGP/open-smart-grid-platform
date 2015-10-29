/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
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
