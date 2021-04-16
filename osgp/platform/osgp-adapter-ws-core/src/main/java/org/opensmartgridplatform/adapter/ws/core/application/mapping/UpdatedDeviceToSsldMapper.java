/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdatedDevice;
import org.opensmartgridplatform.domain.core.entities.Ssld;

public class UpdatedDeviceToSsldMapper extends CustomMapper<UpdatedDevice, Ssld> {

  private final DeviceOutputSettingsMapper deviceOutputSettingsMapper =
      new DeviceOutputSettingsMapper();

  private final GpsCoordinatesMapper gpsCoordinatesMapper = new GpsCoordinatesMapper();

  @Override
  public void mapAtoB(
      final UpdatedDevice source, final Ssld destination, final MappingContext context) {

    this.deviceOutputSettingsMapper.mapAtoB(source, destination, context);
    this.gpsCoordinatesMapper.mapAtoB(source, destination, context);
  }
}
