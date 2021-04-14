/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.db.api.application.services;

import org.opensmartgridplatform.core.db.api.entities.Device;
import org.opensmartgridplatform.core.db.api.repositories.DeviceDataRepository;
import org.opensmartgridplatform.dto.valueobjects.GpsCoordinatesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(value = "osgpCoreDbApiTransactionManager", readOnly = true)
public class DeviceDataService {

  @Autowired private DeviceDataRepository deviceDataRepository;

  public Device findDevice(final String deviceIdentification) {

    return this.deviceDataRepository.findByDeviceIdentification(deviceIdentification);
  }

  public GpsCoordinatesDto getGpsCoordinatesForDevice(final String deviceIdentification) {

    final Device device = this.findDevice(deviceIdentification);

    if (device != null) {
      return new GpsCoordinatesDto(device.getGpsLatitude(), device.getGpsLongitude());
    }

    return null;
  }
}
