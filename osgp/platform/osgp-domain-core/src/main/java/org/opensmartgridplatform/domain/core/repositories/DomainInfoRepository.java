// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.repositories;

import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DomainInfoRepository extends JpaRepository<DomainInfo, Long> {

  DomainInfo findByDomainAndDomainVersion(final String domain, final String domainVersion);
}
