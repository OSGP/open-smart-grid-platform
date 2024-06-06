// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class SmartMeter extends Device {

  /** Serial Version UID */
  private static final long serialVersionUID = -3526823976188640681L;

  @Column(length = 50)
  private String supplier;

  @Column private Short channel;

  @Column private String mbusIdentificationNumber;

  @Column(length = 3)
  private String mbusManufacturerIdentification;

  @Column private Short mbusVersion;

  @Column private Short mbusDeviceTypeIdentification;

  @Column private Short mbusPrimaryAddress;

  public SmartMeter() {
    // Default constructor for hibernate
  }

  public SmartMeter(
      final String deviceIdentification,
      final String alias,
      final Address containerAddress,
      final GpsCoordinates gpsCoordinates) {
    super(deviceIdentification, alias, containerAddress, gpsCoordinates, null);
  }

  public SmartMeter(final String supplier, final Short channel) {
    this.supplier = supplier;
    this.channel = channel;
  }

  @Override
  public void setDeviceType(final String deviceType) {
    this.deviceType = deviceType;
  }

  public String getSupplier() {
    return this.supplier;
  }

  public void setSupplier(final String supplier) {
    this.supplier = supplier;
  }

  /**
   * If this meter has another smart meter as gateway device, it can be connected through one of the
   * gateways M-Bus channels. In such case the channel provides information on how to retrieve data
   * for this meter.
   *
   * <p>An example of where the channel is used, is with a gas meter that is connected on an M-Bus
   * of an energy meter.
   *
   * <p>For meters that are not attached to another smart meters M-Bus channel, the channel is
   * {@code null}.
   *
   * @return the M-Bus channel this smart meter is connected on, on its gateway device, or {@code
   *     null}.
   */
  public Short getChannel() {
    return this.channel;
  }

  public void setChannel(final Short channel) {
    this.channel = channel;
  }

  public void setDeviceIdentification(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  public String getMbusIdentificationNumber() {
    return this.mbusIdentificationNumber;
  }

  public void setMbusIdentificationNumber(final String mbusIdentificationNumber) {
    this.mbusIdentificationNumber = mbusIdentificationNumber;
  }

  public String getMbusManufacturerIdentification() {
    return this.mbusManufacturerIdentification;
  }

  public void setMbusManufacturerIdentification(final String mbusManufacturerIdentification) {
    this.mbusManufacturerIdentification = mbusManufacturerIdentification;
  }

  public Short getMbusVersion() {
    return this.mbusVersion;
  }

  public void setMbusVersion(final Short mbusVersion) {
    this.mbusVersion = mbusVersion;
  }

  public Short getMbusDeviceTypeIdentification() {
    return this.mbusDeviceTypeIdentification;
  }

  public void setMbusDeviceTypeIdentification(final Short mbusDeviceTypeIdentification) {
    this.mbusDeviceTypeIdentification = mbusDeviceTypeIdentification;
  }

  public Short getMbusPrimaryAddress() {
    return this.mbusPrimaryAddress;
  }

  public void setMbusPrimaryAddress(final Short mbusPrimaryAddress) {
    this.mbusPrimaryAddress = mbusPrimaryAddress;
  }
}
