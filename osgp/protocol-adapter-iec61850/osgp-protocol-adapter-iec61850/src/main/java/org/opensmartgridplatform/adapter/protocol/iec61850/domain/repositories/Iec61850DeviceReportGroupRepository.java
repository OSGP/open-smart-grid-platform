//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.domain.repositories;

import java.util.List;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.entities.Iec61850DeviceReportGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Iec61850DeviceReportGroupRepository
    extends JpaRepository<Iec61850DeviceReportGroup, Long> {

  List<Iec61850DeviceReportGroup> findByDeviceIdentificationAndEnabled(
      String deviceIdentification, boolean enabled);

  Iec61850DeviceReportGroup findByDeviceIdentificationAndReportDataSet(
      String deviceIdentification, String reportDataSet);
}
