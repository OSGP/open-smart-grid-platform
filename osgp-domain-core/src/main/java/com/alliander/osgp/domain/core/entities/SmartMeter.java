/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class SmartMeter extends Device {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -3526823976188640681L;

    @Column(length = 50)
    private String supplier;

    @Column
    private Short channel;

    @Column(length = 8)
    private String mbusIdentificationNumber;

    @Column(length = 3)
    private String mbusManufacturerIdentification;

    @Column(length = 3)
    private String mbusVersion;

    @Column(length = 2)
    private String mbusDeviceTypeIdentification;

    public SmartMeter() {
        // Default constructor for hibernate
    }

    public SmartMeter(final String deviceIdentification, final String alias, final String containerCity,
            final String containerPostalCode, final String containerStreet, final String containerNumber,
            final String containerMunicipality, final Float gpsLatitude, final Float gpsLongitude) {
        super(deviceIdentification, alias, containerCity, containerPostalCode, containerStreet, containerNumber,
                containerMunicipality, gpsLatitude, gpsLongitude);
    }

    public SmartMeter(final String supplier, final Short channel) {
        super();
        this.supplier = supplier;
        this.channel = channel;
    }

    public SmartMeter(final String supplier, final Short channel, final String mbusIdentificationNumber,
            final String mbusManufacturerIdentification, final String mbusVersion,
            final String mbusdeviceTypeIdentification) {
        super();
        this.supplier = supplier;
        this.channel = channel;
        this.mbusIdentificationNumber = mbusIdentificationNumber;
        this.mbusManufacturerIdentification = mbusManufacturerIdentification;
        this.mbusVersion = mbusVersion;
        this.mbusDeviceTypeIdentification = mbusdeviceTypeIdentification;
    }

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
     * If this meter has another smart meter as gateway device, it can be
     * connected through one of the gateways M-Bus channels. In such case the
     * channel provides information on how to retrieve data for this meter.
     * <p>
     * An example of where the channel is used, is with a gas meter that is
     * connected on an M-Bus of an energy meter.
     * <p>
     * For meters that are not attached to another smart meters M-Bus channel,
     * the channel is {@code null}.
     *
     * @return the M-Bus channel this smart meter is connected on, on its
     *         gateway device, or {@code null}.
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

    public String getMbusVersion() {
        return this.mbusVersion;
    }

    public void setMbusVersion(final String mbusVersion) {
        this.mbusVersion = mbusVersion;
    }

    public String getMbusDeviceTypeIdentification() {
        return this.mbusDeviceTypeIdentification;
    }

    public void setMbusDeviceTypeIdentification(final String mbusDeviceTypeIdentification) {
        this.mbusDeviceTypeIdentification = mbusDeviceTypeIdentification;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(this.supplier, this.channel, this.mbusIdentificationNumber,
                this.mbusManufacturerIdentification, this.mbusVersion, this.mbusDeviceTypeIdentification);
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj) && Objects.equals(this.supplier, ((SmartMeter) obj).supplier)
                && Objects.equals(this.channel, ((SmartMeter) obj).channel) && this.compareMbusAttributes(obj);
    }

    private boolean compareMbusAttributes(final Object obj) {
        return Objects.equals(this.mbusIdentificationNumber, ((SmartMeter) obj).mbusIdentificationNumber)
                && Objects.equals(this.mbusManufacturerIdentification,
                        ((SmartMeter) obj).mbusManufacturerIdentification)
                && Objects.equals(this.mbusVersion, ((SmartMeter) obj).mbusVersion)
                && Objects.equals(this.mbusDeviceTypeIdentification, ((SmartMeter) obj).mbusDeviceTypeIdentification);
    }
}
