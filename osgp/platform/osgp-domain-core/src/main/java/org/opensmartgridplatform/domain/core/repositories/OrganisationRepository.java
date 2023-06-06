// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.repositories;

import java.util.List;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Organization repository interface */
@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Long> {
  Organisation findByOrganisationIdentification(String organisationIdentification);

  Organisation findByName(String name);

  List<Organisation> findByOrderByOrganisationIdentification();

  List<Organisation> findByEnabled(boolean enabled);
}
