// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.repositories;

import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.opensmartgridplatform.domain.core.entities.FirmwareFileFirmwareModule;
import org.opensmartgridplatform.domain.core.entities.FirmwareFileFirmwareModule.FirmwareFileFirmwareModuleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FirmwareFileFirmwareModuleRepository
    extends JpaRepository<FirmwareFileFirmwareModule, FirmwareFileFirmwareModuleId> {

  FirmwareFileFirmwareModule findByFirmwareFile(FirmwareFile firmwareFile);
}
