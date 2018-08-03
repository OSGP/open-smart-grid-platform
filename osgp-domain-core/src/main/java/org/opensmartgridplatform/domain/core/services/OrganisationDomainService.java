/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.OrganisationRepository;
import org.opensmartgridplatform.domain.core.validation.Identification;

@Service
@Validated
@Transactional(value = "transactionManager")
public class OrganisationDomainService {

    @Autowired
    private OrganisationRepository organisationRepository;

    public Organisation searchOrganisation(@Identification final String organisationIdentification) throws UnknownEntityException {

        final Organisation organisation = this.organisationRepository.findByOrganisationIdentification(organisationIdentification);
        if (organisation == null) {
            throw new UnknownEntityException(Organisation.class, organisationIdentification);
        }

        return organisation;
    }
}
