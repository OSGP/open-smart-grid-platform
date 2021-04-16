/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

/**
 * This DTO contains a number of attributes that may contain input that is usable looking for actual
 * values that could be described as {@link ChannelElementValuesDto}.
 */
public class MbusChannelElementsDto implements ActionRequestDto {

  private static final long serialVersionUID = 5377631203726277889L;

  private final Short primaryAddress;
  private final String mbusDeviceIdentification;
  private final String mbusIdentificationNumber;
  private final String mbusManufacturerIdentification;
  private final Short mbusVersion;
  private final Short mbusDeviceTypeIdentification;

  public MbusChannelElementsDto(
      final Short primaryAddress,
      final String mbusDeviceIdentification,
      final String mbusIdentificationNumber,
      final String mbusManufacturerIdentification,
      final Short mbusVersion,
      final Short mbusDeviceTypeIdentification) {
    this.primaryAddress = primaryAddress;
    this.mbusDeviceIdentification = mbusDeviceIdentification;
    this.mbusIdentificationNumber = mbusIdentificationNumber;
    this.mbusManufacturerIdentification = mbusManufacturerIdentification;
    this.mbusVersion = mbusVersion;
    this.mbusDeviceTypeIdentification = mbusDeviceTypeIdentification;
  }

  @Override
  public String toString() {
    return String.format(
        "MbusChannelElementsDto[mbusDeviceIdentification=%s, mbusIdentificationNumber=%s, mbusManufacturerIdentification=%s, mbusVersion=%s, mbusDeviceTypeIdentification=%s]",
        this.mbusDeviceIdentification,
        this.mbusIdentificationNumber,
        this.mbusManufacturerIdentification,
        this.mbusVersion,
        this.mbusDeviceTypeIdentification);
  }

  public String getMbusDeviceIdentification() {
    return this.mbusDeviceIdentification;
  }

  public String getMbusIdentificationNumber() {
    return this.mbusIdentificationNumber;
  }

  public boolean hasMbusIdentificationNumber() {
    return this.mbusIdentificationNumber != null;
  }

  public String getMbusManufacturerIdentification() {
    return this.mbusManufacturerIdentification;
  }

  public boolean hasMbusManufacturerIdentification() {
    return this.mbusManufacturerIdentification != null;
  }

  public Short getMbusVersion() {
    return this.mbusVersion;
  }

  public boolean hasMbusVersion() {
    return this.mbusVersion != null;
  }

  public Short getMbusDeviceTypeIdentification() {
    return this.mbusDeviceTypeIdentification;
  }

  public boolean hasMbusDeviceTypeIdentification() {
    return this.mbusDeviceTypeIdentification != null;
  }

  public Short getPrimaryAddress() {
    return this.primaryAddress;
  }

  public boolean hasPrimaryAddress() {
    return this.primaryAddress != null;
  }
}
