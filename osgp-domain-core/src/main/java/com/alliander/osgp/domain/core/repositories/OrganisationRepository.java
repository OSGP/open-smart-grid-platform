package com.alliander.osgp.domain.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.domain.core.entities.Organisation;

/**
 * Organisation repository interface
 */
@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Long> {
    Organisation findByOrganisationIdentification(String organisationIdentification);

    List<Organisation> findByEnabled(boolean enabled);
}
