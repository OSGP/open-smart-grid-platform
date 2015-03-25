package com.alliander.osgp.domain.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.validation.Identification;

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
