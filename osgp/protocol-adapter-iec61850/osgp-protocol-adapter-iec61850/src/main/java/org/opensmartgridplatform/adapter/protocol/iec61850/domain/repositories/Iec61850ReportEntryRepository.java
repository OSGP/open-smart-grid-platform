//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.domain.repositories;

import org.opensmartgridplatform.adapter.protocol.iec61850.domain.entities.Iec61850ReportEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Iec61850ReportEntryRepository extends JpaRepository<Iec61850ReportEntry, Long> {

  Iec61850ReportEntry findByDeviceIdentificationAndReportId(
      String deviceIdentification, String reportId);
}
