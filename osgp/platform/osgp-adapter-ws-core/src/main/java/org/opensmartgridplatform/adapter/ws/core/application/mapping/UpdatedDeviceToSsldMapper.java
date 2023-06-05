// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
