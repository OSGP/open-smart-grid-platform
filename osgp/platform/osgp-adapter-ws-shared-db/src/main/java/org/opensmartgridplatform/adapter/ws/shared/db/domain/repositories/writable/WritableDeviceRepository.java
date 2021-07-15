/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable;

import java.util.List;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WritableDeviceRepository extends JpaRepository<Device, Long> {
  Device findByDeviceIdentification(String deviceIdentification);

  List<Device> findByDeviceModel(DeviceModel removedDeviceModel);
}
