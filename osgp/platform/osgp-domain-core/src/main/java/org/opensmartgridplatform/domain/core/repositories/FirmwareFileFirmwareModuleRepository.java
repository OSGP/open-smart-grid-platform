/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
