/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable;

import java.util.List;

import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WritableFirmwareFileRepository
        extends JpaRepository<FirmwareFile, Long>, JpaSpecificationExecutor<FirmwareFile> {

    @Query("SELECT ff FROM FirmwareFile ff WHERE :deviceModel MEMBER OF ff.deviceModels")
    List<FirmwareFile> findByDeviceModel(@Param("deviceModel") DeviceModel deviceModel);

    @Query("SELECT ff FROM FirmwareFile ff WHERE ff.filename = :filename AND :deviceModel MEMBER OF ff.deviceModels")
    List<FirmwareFile> findByDeviceModelAndFilename(@Param("deviceModel") DeviceModel deviceModel,
            @Param("filename") String filename);
}
