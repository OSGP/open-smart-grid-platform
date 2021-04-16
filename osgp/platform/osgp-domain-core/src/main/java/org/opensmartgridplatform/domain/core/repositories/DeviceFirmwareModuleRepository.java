/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.repositories;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceFirmwareModule;
import org.opensmartgridplatform.domain.core.entities.DeviceFirmwareModule.DeviceFirmwareModuleId;
import org.opensmartgridplatform.domain.core.entities.FirmwareModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceFirmwareModuleRepository
    extends JpaRepository<DeviceFirmwareModule, DeviceFirmwareModuleId> {

  @Query(
      value =
          "SELECT dfm FROM DeviceFirmwareModule dfm LEFT JOIN FETCH dfm.firmwareModule fm "
              + "WHERE dfm.device = :device ORDER BY fm.description ASC")
  List<DeviceFirmwareModule> findByDevice(@Param("device") Device device);

  default Map<FirmwareModule, String> findVersionPerFirmwareModule(final Device device) {
    return this.findByDevice(device).stream()
        .collect(
            Collectors.toMap(
                DeviceFirmwareModule::getFirmwareModule, DeviceFirmwareModule::getModuleVersion));
  }
}
