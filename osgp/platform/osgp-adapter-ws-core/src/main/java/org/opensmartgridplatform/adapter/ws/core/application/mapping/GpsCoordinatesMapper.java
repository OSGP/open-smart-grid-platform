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
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdatedDevice;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;

public class GpsCoordinatesMapper extends CustomMapper<UpdatedDevice, Ssld> {

  @Override
  public void mapAtoB(
      final UpdatedDevice source, final Ssld destination, final MappingContext context) {

    if (StringUtils.isNotEmpty(source.getGpsLatitude())
        && StringUtils.isNotEmpty(source.getGpsLongitude())) {
      final Float latitude = Float.valueOf(source.getGpsLatitude());
      final Float longitude = Float.valueOf(source.getGpsLongitude());
      destination.setGpsCoordinates(new GpsCoordinates(latitude, longitude));
    }
  }
}
