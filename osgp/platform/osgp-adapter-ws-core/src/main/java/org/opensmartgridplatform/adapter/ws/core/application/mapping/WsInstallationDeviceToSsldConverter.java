//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.Device;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WsInstallationDeviceToSsldConverter extends CustomConverter<Device, Ssld> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(WsInstallationDeviceToSsldConverter.class);

  @Override
  public Ssld convert(
      final Device source,
      final Type<? extends Ssld> destinationType,
      final MappingContext mappingContext) {

    LOGGER.debug(
        "Converting WS Installation Device into SSLD [{}]", source.getDeviceIdentification());

    final String deviceIdentification = source.getDeviceIdentification();
    final String alias = source.getAlias();
    final Address containerAddress =
        this.mapperFacade.map(source.getContainerAddress(), Address.class);
    final GpsCoordinates gpsCoordinates =
        new GpsCoordinates(source.getGpsLatitude(), source.getGpsLongitude());
    final Ssld ssld = new Ssld(deviceIdentification, alias, containerAddress, gpsCoordinates, null);
    ssld.setPublicKeyPresent(source.isPublicKeyPresent());
    return ssld;
  }
}
