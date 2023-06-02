//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceModelRepository;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WsInstallationLmdToLmdConverter
    extends CustomConverter<
        org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.LightMeasurementDevice,
        LightMeasurementDevice> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(WsInstallationLmdToLmdConverter.class);

  private final WritableDeviceModelRepository writableDeviceModelRepository;

  public WsInstallationLmdToLmdConverter(
      final WritableDeviceModelRepository writableDeviceModelRepository) {
    this.writableDeviceModelRepository = writableDeviceModelRepository;
  }

  @Override
  public LightMeasurementDevice convert(
      final org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation
              .LightMeasurementDevice
          source,
      final Type<? extends LightMeasurementDevice> destinationType,
      final MappingContext mappingContext) {

    LOGGER.debug("Converting WS Installation LMD into LMD [{}]", source.getDeviceIdentification());

    final String deviceIdentification = source.getDeviceIdentification();
    final String alias = source.getAlias();
    final Address containerAddress =
        this.mapperFacade.map(source.getContainerAddress(), Address.class);
    final GpsCoordinates gpsCoordinates =
        new GpsCoordinates(source.getGpsLatitude(), source.getGpsLongitude());
    final LightMeasurementDevice lmd =
        new LightMeasurementDevice(
            deviceIdentification, alias, containerAddress, gpsCoordinates, null);
    lmd.setDescription(source.getDescription());
    lmd.setCode(source.getCode());
    lmd.setColor(source.getColor());
    lmd.setDigitalInput(source.getDigitalInput());

    if (source.getDeviceModel() != null) {
      final DeviceModel deviceModel =
          this.writableDeviceModelRepository.findByManufacturerCodeAndModelCode(
              source.getDeviceModel().getManufacturer(), source.getDeviceModel().getModelCode());
      lmd.setDeviceModel(deviceModel);
    }

    return lmd;
  }
}
