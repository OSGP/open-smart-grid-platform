// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.db.api.iec61850.repositories;

import org.opensmartgridplatform.core.db.api.iec61850.entities.Ssld;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SsldDataRepository extends JpaRepository<Ssld, Long> {
  Ssld findByDeviceIdentification(String deviceIdentification);
}
