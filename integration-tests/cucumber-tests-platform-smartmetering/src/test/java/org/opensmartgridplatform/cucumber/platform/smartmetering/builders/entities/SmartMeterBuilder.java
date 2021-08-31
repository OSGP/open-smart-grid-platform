/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.builders.entities;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getShort;

import java.util.Map;
import org.opensmartgridplatform.cucumber.platform.core.builders.CucumberBuilder;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;

public class SmartMeterBuilder extends BaseDeviceBuilder<SmartMeterBuilder>
    implements CucumberBuilder<SmartMeter> {

  private static final int HEX_RADIX = 16;

  private String supplier;
  private Short channel;
  private Long mbusIdentificationNumber;
  private String mbusManufacturerIdentification;
  private Short mbusVersion;
  private Short mbusDeviceTypeIdentification;
  private Short mbusPrimaryAddress;

  public SmartMeterBuilder setSupplier(final String supplier) {
    this.supplier = supplier;
    return this;
  }

  public SmartMeterBuilder setChannel(final Short channel) {
    this.channel = channel;
    return this;
  }

  public SmartMeterBuilder setMbusIdentificationNumber(final String value) {
    this.mbusIdentificationNumber = Long.parseLong(value, HEX_RADIX);
    return this;
  }

  public SmartMeterBuilder setMbusManufacturerIdentification(final String value) {
    this.mbusManufacturerIdentification = value;
    return this;
  }

  public SmartMeterBuilder setMbusVersion(final Short value) {
    this.mbusVersion = value;
    return this;
  }

  public SmartMeterBuilder setMbusDeviceTypeIdentification(final Short value) {
    this.mbusDeviceTypeIdentification = value;
    return this;
  }

  public SmartMeterBuilder setMbusPrimaryAddress(final Short value) {
    this.mbusPrimaryAddress = value;
    return this;
  }

  @Override
  public SmartMeter build() {
    final SmartMeter device =
        new SmartMeter(
            this.deviceIdentification,
            this.alias,
            new Address(
                this.containerCity,
                this.containerPostalCode,
                this.containerStreet,
                this.containerNumber,
                this.containerNumberAddition,
                this.containerMunicipality),
            new GpsCoordinates(this.gpsLatitude, this.gpsLongitude));

    device.setActivated(this.isActivated);
    device.updateRegistrationData(this.networkAddress, this.deviceType);

    // After updateRegistrationData because that sets deviceLifecyleStatus
    // to IN_USE again.
    device.setDeviceLifecycleStatus(this.deviceLifeCycleStatus);
    device.updateProtocol(this.protocolInfo);
    device.updateInMaintenance(this.inMaintenance);
    device.setVersion(this.version);
    device.setDeviceModel(this.deviceModel);
    device.setTechnicalInstallationDate(this.technicalInstallationDate);

    device.setSupplier(this.supplier);
    device.setChannel(this.channel);

    device.setMbusIdentificationNumber(this.mbusIdentificationNumber);
    device.setMbusManufacturerIdentification(this.mbusManufacturerIdentification);
    device.setMbusVersion(this.mbusVersion);
    device.setMbusDeviceTypeIdentification(this.mbusDeviceTypeIdentification);
    device.setMbusPrimaryAddress(this.mbusPrimaryAddress);

    return device;
  }

  @Override
  public SmartMeterBuilder withSettings(final Map<String, String> inputSettings) {
    super.withSettings(inputSettings);

    if (inputSettings.containsKey(PlatformSmartmeteringKeys.CHANNEL)) {
      this.setChannel(getShort(inputSettings, PlatformSmartmeteringKeys.CHANNEL));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.SUPPLIER)) {
      this.setSupplier(inputSettings.get(PlatformSmartmeteringKeys.SUPPLIER));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.MBUS_IDENTIFICATION_NUMBER)) {
      this.setMbusIdentificationNumber(
          inputSettings.get(PlatformSmartmeteringKeys.MBUS_IDENTIFICATION_NUMBER));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.MBUS_MANUFACTURER_IDENTIFICATION)) {
      this.setMbusManufacturerIdentification(
          inputSettings.get(PlatformSmartmeteringKeys.MBUS_MANUFACTURER_IDENTIFICATION));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.MBUS_VERSION)) {
      this.setMbusVersion(getShort(inputSettings, PlatformSmartmeteringKeys.MBUS_VERSION));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.MBUS_DEVICE_TYPE_IDENTIFICATION)) {
      this.setMbusDeviceTypeIdentification(
          getShort(inputSettings, PlatformSmartmeteringKeys.MBUS_DEVICE_TYPE_IDENTIFICATION));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.MBUS_PRIMARY_ADDRESS)) {
      this.setMbusPrimaryAddress(
          getShort(inputSettings, PlatformSmartmeteringKeys.MBUS_PRIMARY_ADDRESS));
    }

    return this;
  }
}
