/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Firmware;

@Repository
public interface FirmwareRepository extends JpaRepository<Firmware, Long> {

    public List<Firmware> findByModuleVersionCommAndModuleVersionMaAndModuleVersionFunc(final String moduleVersionComm,
            final String moduleVersionMa, final String moduleVersionFunc);

    public Firmware findByDeviceModelAndModuleVersionCommAndModuleVersionMaAndModuleVersionFunc(
            final DeviceModel deviceModel, final String moduleVersionComm, final String moduleVersionMa,
            final String moduleVersionFunc);

    public Firmware findByFilename(final String filename);

    public Firmware findByIdentification(final String identification);
}
