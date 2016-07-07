/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceModelFirmware;
import com.alliander.osgp.domain.core.entities.Firmware;

@Repository
public interface WritableFirmwareRepository extends JpaRepository<Firmware, Long> {
    List<Firmware> findByDevice(Device device);

    List<Firmware> findByDeviceModelFirmware(DeviceModelFirmware deviceModelFirmware);
}
