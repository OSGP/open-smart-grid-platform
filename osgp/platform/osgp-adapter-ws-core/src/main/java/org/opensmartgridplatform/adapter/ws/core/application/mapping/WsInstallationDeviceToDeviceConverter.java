/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import java.util.Objects;
import java.util.Optional;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.Device;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceModelRepository;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;

public class WsInstallationDeviceToDeviceConverter
    extends BidirectionalConverter<Device, org.opensmartgridplatform.domain.core.entities.Device> {

  private final SsldRepository ssldRepository;

  private final WritableDeviceModelRepository writableDeviceModelRepository;

  public WsInstallationDeviceToDeviceConverter(
      final SsldRepository ssldRepository,
      final WritableDeviceModelRepository writableDeviceModelRepository) {
    this.ssldRepository = ssldRepository;
    this.writableDeviceModelRepository = writableDeviceModelRepository;
  }

  @Override
  public Device convertFrom(
      final org.opensmartgridplatform.domain.core.entities.Device source,
      final Type<Device> destinationType,
      final MappingContext context) {

    Device destination = null;
    if (source != null) {
      destination = new Device();
      destination.setDeviceIdentification(source.getDeviceIdentification());
      destination.setAlias(source.getAlias());

      destination.setContainerAddress(
          this.mapperFacade.map(
              source.getContainerAddress(),
              org.opensmartgridplatform.adapter.ws.schema.core.common.Address.class));

      if (!Objects.isNull(source.getGpsCoordinates())) {
        final GpsCoordinates gpsCoordinates = source.getGpsCoordinates();
        destination.setGpsLatitude(gpsCoordinates.getLatitude());
        destination.setGpsLongitude(gpsCoordinates.getLongitude());
      }

      destination.setActivated(source.isActivated());

      final Optional<Ssld> ssld = this.ssldRepository.findById(source.getId());
      if (ssld.isPresent()) {
        destination.setHasSchedule(ssld.get().getHasSchedule());
      }

      return destination;
    }
    return null;
  }

  @Override
  public org.opensmartgridplatform.domain.core.entities.Device convertTo(
      final Device source,
      final Type<org.opensmartgridplatform.domain.core.entities.Device> destinationType,
      final MappingContext context) {

    org.opensmartgridplatform.domain.core.entities.Device destination = null;

    if (source != null) {
      final Address address = this.mapperFacade.map(source.getContainerAddress(), Address.class);
      destination =
          new org.opensmartgridplatform.domain.core.entities.Device(
              source.getDeviceIdentification(),
              source.getAlias(),
              address,
              new GpsCoordinates(source.getGpsLatitude(), source.getGpsLongitude()),
              null);

      final DeviceModel deviceModel =
          this.writableDeviceModelRepository.findByManufacturerCodeAndModelCode(
              source.getDeviceModel().getManufacturer(), source.getDeviceModel().getModelCode());

      if (deviceModel == null) {
        throw new AssertionError(
            "Model code \""
                + source.getDeviceModel().getModelCode()
                + "\" and Manufacturer \""
                + source.getDeviceModel().getManufacturer()
                + "\" do not identify an existing device model.");
      } else {
        destination.setDeviceModel(deviceModel);
      }

      return destination;
    }
    return null;
  }

  @Override
  public int hashCode() {
    return super.hashCode() + Objects.hash(this.ssldRepository, this.writableDeviceModelRepository);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof WsInstallationDeviceToDeviceConverter)) {
      return false;
    }

    final WsInstallationDeviceToDeviceConverter other = (WsInstallationDeviceToDeviceConverter) obj;
    return super.equals(other)
        && Objects.equals(this.ssldRepository, other.ssldRepository)
        && Objects.equals(this.writableDeviceModelRepository, other.writableDeviceModelRepository);
  }
}
