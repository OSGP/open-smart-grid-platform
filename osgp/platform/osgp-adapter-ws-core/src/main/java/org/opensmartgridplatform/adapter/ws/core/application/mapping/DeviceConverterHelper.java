// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.datatype.XMLGregorianCalendar;
import ma.glasnost.orika.MapperFacade;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceModel;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Manufacturer;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;

class DeviceConverterHelper<T extends org.opensmartgridplatform.domain.core.entities.Device> {

  private final Class<T> clazz;
  private MapperFacade mapper;

  public DeviceConverterHelper(final Class<T> clazz) {
    this.clazz = clazz;
  }

  public void setMapperFacade(final MapperFacade mapper) {
    this.mapper = mapper;
  }

  @SuppressWarnings("unchecked")
  T initEntity(final Device source) {
    T destination;

    final Address containerAddress = this.mapper.map(source.getContainerAddress(), Address.class);

    GpsCoordinates gpsCoordinates = null;
    if (source.getGpsLatitude() != null && source.getGpsLongitude() != null) {
      gpsCoordinates =
          new GpsCoordinates(
              Float.valueOf(source.getGpsLatitude()), Float.valueOf(source.getGpsLongitude()));
    }

    if (this.clazz.isAssignableFrom(SmartMeter.class)) {
      destination =
          (T)
              new SmartMeter(
                  source.getDeviceIdentification(),
                  source.getAlias(),
                  containerAddress,
                  gpsCoordinates);
    } else {
      destination =
          (T)
              new Ssld(
                  source.getDeviceIdentification(),
                  source.getAlias(),
                  containerAddress,
                  gpsCoordinates,
                  null);
    }

    if (source.isActivated() != null) {
      destination.setActivated(source.isActivated());
    }

    if (source.getDeviceLifecycleStatus() != null) {
      destination.setDeviceLifecycleStatus(
          DeviceLifecycleStatus.valueOf(source.getDeviceLifecycleStatus().name()));
    }

    destination.updateRegistrationData(destination.getNetworkAddress(), source.getDeviceType());

    if (source.getTechnicalInstallationDate() != null) {
      destination.setTechnicalInstallationDate(
          source.getTechnicalInstallationDate().toGregorianCalendar().getTime());
    }

    destination.setDeviceModel(
        this.mapper.map(
            source.getDeviceModel(),
            org.opensmartgridplatform.domain.core.entities.DeviceModel.class));

    return destination;
  }

  Device initJaxb(final T source) {

    final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device destination =
        new org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device();
    destination.setAlias(source.getAlias());
    destination.setActivated(source.isActivated());
    destination.setDeviceLifecycleStatus(
        org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceLifecycleStatus
            .valueOf(source.getDeviceLifecycleStatus().name()));

    destination.setContainerAddress(
        this.mapper.map(
            source.getContainerAddress(),
            org.opensmartgridplatform.adapter.ws.schema.core.common.Address.class));

    destination.setDeviceIdentification(source.getDeviceIdentification());
    destination.setDeviceType(source.getDeviceType());
    destination.setTechnicalInstallationDate(
        this.mapper.map(source.getTechnicalInstallationDate(), XMLGregorianCalendar.class));

    if (!Objects.isNull(source.getGpsCoordinates())) {
      final GpsCoordinates gpsCoordinates = source.getGpsCoordinates();
      if (gpsCoordinates.getLatitude() != null) {
        destination.setGpsLatitude(Float.toString(gpsCoordinates.getLatitude()));
      }
      if (gpsCoordinates.getLongitude() != null) {
        destination.setGpsLongitude(Float.toString(gpsCoordinates.getLongitude()));
      }
    }

    destination.setNetworkAddress(
        source.getNetworkAddress() == null ? null : source.getNetworkAddress().toString());
    destination.setOwner(source.getOwner() == null ? "" : source.getOwner().getName());
    destination.getOrganisations().addAll(source.getOrganisations());

    destination.setInMaintenance(source.isInMaintenance());

    final List<
            org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceAuthorization>
        deviceAuthorizations = new ArrayList<>();
    for (final DeviceAuthorization deviceAuthorisation : source.getAuthorizations()) {
      final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceAuthorization
          newDeviceAuthorization =
              new org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement
                  .DeviceAuthorization();

      newDeviceAuthorization.setFunctionGroup(deviceAuthorisation.getFunctionGroup().name());
      newDeviceAuthorization.setOrganisation(
          deviceAuthorisation.getOrganisation().getOrganisationIdentification());
      deviceAuthorizations.add(newDeviceAuthorization);
    }
    destination.getDeviceAuthorizations().addAll(deviceAuthorizations);

    if (source.getDeviceModel() != null) {
      final DeviceModel deviceModel = new DeviceModel();
      deviceModel.setDescription(source.getDeviceModel().getDescription());
      if (source.getDeviceModel().getManufacturer() != null) {
        final Manufacturer manufacturer = new Manufacturer();
        manufacturer.setManufacturerId(source.getDeviceModel().getManufacturer().getCode());
        manufacturer.setName(source.getDeviceModel().getManufacturer().getName());
        manufacturer.setUsePrefix(source.getDeviceModel().getManufacturer().isUsePrefix());
        deviceModel.setManufacturer(manufacturer);
      }
      deviceModel.setModelCode(source.getDeviceModel().getModelCode());
      destination.setDeviceModel(deviceModel);
    }

    destination.setLastCommunicationTime(
        this.mapper.map(source.getLastSuccessfulConnectionTimestamp(), XMLGregorianCalendar.class));

    if (source instanceof LightMeasurementDevice) {
      final LightMeasurementDevice sourceLmd = (LightMeasurementDevice) source;
      final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.LightMeasurementDevice
          destinationLmd =
              new org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement
                  .LightMeasurementDevice();
      destinationLmd.setDescription(sourceLmd.getDescription());
      destinationLmd.setCode(sourceLmd.getCode());
      destinationLmd.setColor(sourceLmd.getColor());
      destinationLmd.setDigitalInput(sourceLmd.getDigitalInput());
      destinationLmd.setLastCommunicationTime(
          this.mapper.map(sourceLmd.getLastCommunicationTime(), XMLGregorianCalendar.class));
      destination.setLightMeasurementDevice(destinationLmd);
    }

    return destination;
  }
}
