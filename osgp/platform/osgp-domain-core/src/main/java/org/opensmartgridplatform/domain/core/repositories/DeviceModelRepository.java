/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.repositories;

import java.util.List;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceModelRepository
    extends JpaRepository<DeviceModel, Long>, JpaSpecificationExecutor<DeviceModel> {

  List<DeviceModel> findByManufacturer(Manufacturer manufacturer);

  List<DeviceModel> findByModelCode(String modelCode);

  DeviceModel findByManufacturerAndModelCode(Manufacturer manufacturer, String modelCode);
}
