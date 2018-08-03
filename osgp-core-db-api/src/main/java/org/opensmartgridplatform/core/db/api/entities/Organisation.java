/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.db.api.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
public class Organisation extends AbstractEntity {

    /**
     * SerialVersionID for serialization
     */
    private static final long serialVersionUID = -1097307978466479033L;

    @Column()
    private String organisationIdentification;

    public Organisation() {
        // Default constructor
    }

    public Organisation(final String organisationIdentification) {
        this.organisationIdentification = organisationIdentification;
    }

    /**
     * Gets the organisation's identification
     *
     * @return the identification
     */
    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }
}
