//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.repositories;

import java.util.List;
import org.opensmartgridplatform.domain.core.entities.ScheduledTaskWithoutData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledTaskWithoutDataRepository
    extends JpaRepository<ScheduledTaskWithoutData, Long> {

  List<ScheduledTaskWithoutData> findByDeviceIdentification(String deviceIdentification);

  List<ScheduledTaskWithoutData> findByOrganisationIdentification(
      String organisationIdentification);
}
