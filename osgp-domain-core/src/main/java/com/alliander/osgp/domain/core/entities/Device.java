/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.entities;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
import com.alliander.osgp.domain.core.valueobjects.RelayType;
import com.alliander.osgp.shared.domain.entities.AbstractEntity;

// TODO: Refactor: Create Container and Gps classes

@Entity
public class Device extends AbstractEntity implements DeviceInterface, LocationInformationInterface,
        NetworkAddressInterface {

    /**
     * Device type indicator for PSLD
     */
    public static final String PSLD_TYPE = "PSLD";

    /**
     * Device type indicator for SSLD
     */
    public static final String SSLD_TYPE = "SSLD";

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -1067112091560627041L;
    @Identification
    @Column(unique = true, nullable = false, length = 40)
    private String deviceIdentification;

    @Column(length = 255)
    private String containerCity;
    @Column(length = 255)
    private String containerStreet;
    @Column(length = 10)
    private String containerPostalCode;
    @Column(length = 255)
    private String containerNumber;

    @Column
    private Float gpsLatitude;
    @Column
    private Float gpsLongitude;

    private String deviceType;

    @Column(length = 50)
    @Type(type = "com.alliander.osgp.shared.hibernate.InetAddressUserType")
    private InetAddress networkAddress;

    private boolean isActivated;

    @OneToMany(mappedBy = "device", targetEntity = DeviceAuthorization.class, fetch = FetchType.EAGER)
    private final List<DeviceAuthorization> authorizations = new ArrayList<DeviceAuthorization>();

    @ElementCollection
    @CollectionTable(name = "device_output_setting", joinColumns = @JoinColumn(name = "device_id"))
    private List<DeviceOutputSetting> outputSettings = new ArrayList<>();

    private boolean hasSchedule;

    @Transient
    private final List<String> organisations = new ArrayList<String>();

    @Column()
    private boolean hasPublicKey;

    @ManyToOne()
    @JoinColumn(name = "protocol_info_id")
    private ProtocolInfo protocolInfo;

    public Device() {
        // Default constructor
    }

    public Device(final String deviceIdentification, final String deviceType, final InetAddress networkAddress,
            final boolean activated, final boolean hasSchedule) {
        this.deviceIdentification = deviceIdentification;
        this.deviceType = deviceType;
        this.networkAddress = networkAddress;
        this.isActivated = activated;
        this.hasSchedule = hasSchedule;
    }

    public Device(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public Device(final String deviceIdentification, final String containerCity, final String containerPostalCode,
            final String containerStreet, final String containerNumber, final Float gpsLatitude,
            final Float gpsLongitude) {
        this.deviceIdentification = deviceIdentification;
        this.containerCity = containerCity;
        this.containerPostalCode = containerPostalCode;
        this.containerStreet = containerStreet;
        this.containerNumber = containerNumber;
        this.gpsLatitude = gpsLatitude;
        this.gpsLongitude = gpsLongitude;

    }

    @Override
    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    @Override
    public String getContainerPostalCode() {
        return this.containerPostalCode;
    }

    @Override
    public String getContainerCity() {
        return this.containerCity;
    }

    @Override
    public String getContainerStreet() {
        return this.containerStreet;
    }

    @Override
    public String getContainerNumber() {
        return this.containerNumber;
    }

    @Override
    public Float getGpsLatitude() {
        return this.gpsLatitude;
    }

    @Override
    public Float getGpsLongitude() {
        return this.gpsLongitude;
    }

    @Override
    public String getDeviceType() {
        return this.deviceType;
    }

    @Override
    public InetAddress getNetworkAddress() {
        return this.networkAddress;
    }

    @Override
    public String getIpAddress() {
        return this.networkAddress == null ? null : this.networkAddress.getHostAddress();
    }

    public boolean isActivated() {
        return this.isActivated;
    }

    public boolean getHasSchedule() {
        return this.hasSchedule;
    }

    public boolean isPublicKeyPresent() {
        return this.hasPublicKey;
    }

    public void setPublicKeyPresent(final boolean isPublicKeyPresent) {
        this.hasPublicKey = isPublicKeyPresent;
    }

    @Override
    public ProtocolInfo getProtocolInfo() {
        return this.protocolInfo;
    }

    public void updateMetaData(final String containerCity, final String containerPostalCode,
            final String containerStreet, final String containerNumber, final Float gpsLatitude,
            final Float gpsLongitude) {
        this.containerCity = containerCity;
        this.containerPostalCode = containerPostalCode;
        this.containerStreet = containerStreet;
        this.containerNumber = containerNumber;
        this.gpsLatitude = gpsLatitude;
        this.gpsLongitude = gpsLongitude;

    }

    public void updateRegistrationData(final InetAddress networkAddress, final String deviceType) {
        // Set the incoming values.
        this.networkAddress = networkAddress;
        this.deviceType = deviceType;
        this.isActivated = true;
    }

    public void updateOutputSettings(final List<DeviceOutputSetting> outputSettings) {
        this.outputSettings = outputSettings;
    }

    public void updateProtocol(final ProtocolInfo protocolInfo) {
        this.protocolInfo = protocolInfo;
    }

    public void clearNetworkAddress() {
        this.networkAddress = null;
    }

    @Override
    public List<DeviceAuthorization> getAuthorizations() {
        return this.authorizations;
    }

    public List<DeviceOutputSetting> getOutputSettings() {
        if (this.outputSettings == null || this.outputSettings.isEmpty()) {
            return Collections.unmodifiableList(this.createDefaultConfiguration());
        }

        return Collections.unmodifiableList(this.outputSettings);
    }

    /**
     * Get the owner organisation name of the device.
     *
     * @return The organisation name when an owner was set, "" otherwise.
     */
    @Override
    public String getOwner() {
        String retval = "";

        if (this.authorizations != null) {
            for (final DeviceAuthorization authorization : this.authorizations) {
                if (authorization.getFunctionGroup().equals(DeviceFunctionGroup.OWNER)) {
                    retval = authorization.getOrganisation().getName();
                }
            }
        }

        return retval;
    }

    @Override
    public DeviceAuthorization addAuthorization(final Organisation organisation, final DeviceFunctionGroup functionGroup) {
        // TODO: Make sure that there is only one owner authorization.
        final DeviceAuthorization authorization = new DeviceAuthorization(this, organisation, functionGroup);
        this.authorizations.add(authorization);
        return authorization;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Device device = (Device) o;
        if (this.isActivated != device.isActivated) {
            return false;
        }
        if (this.hasSchedule != device.hasSchedule) {
            return false;
        }
        if (this.authorizations != null ? !this.authorizations.equals(device.authorizations)
                : device.authorizations != null) {
            return false;
        }
        if (this.deviceIdentification != null ? !this.deviceIdentification.equals(device.deviceIdentification)
                : device.deviceIdentification != null) {
            return false;
        }
        if (this.deviceType != null ? !this.deviceType.equals(device.deviceType) : device.deviceType != null) {
            return false;
        }
        if (this.networkAddress != null ? !this.networkAddress.equals(device.networkAddress)
                : device.networkAddress != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = this.deviceIdentification != null ? this.deviceIdentification.hashCode() : 0;
        result = 31 * result + (this.deviceType != null ? this.deviceType.hashCode() : 0);
        result = 31 * result + (this.networkAddress != null ? this.networkAddress.hashCode() : 0);
        result = 31 * result + (this.isActivated ? 1 : 0);
        result = 31 * result + (this.hasSchedule ? 1 : 0);
        result = 31 * result + (this.authorizations != null ? this.authorizations.hashCode() : 0);
        return result;
    }

    public void setHasSchedule(final boolean hasSchedule) {
        this.hasSchedule = hasSchedule;
    }

    /**
     * Get the organisations that are authorized for this device.
     *
     * @return List of OrganisationIdentification of organisations that are
     *         authorized for this device.
     */
    @Override
    @Transient
    public List<String> getOrganisations() {
        return this.organisations;
    }

    public void addOrganisation(final String organisationIdentification) {
        this.organisations.add(organisationIdentification);
    }

    /**
     * Create default configuration for a device (based on type).
     *
     * @return default configuration
     */
    private List<DeviceOutputSetting> createDefaultConfiguration() {
        final List<DeviceOutputSetting> defaultConfiguration = new ArrayList<>();

        if (this.deviceType.equalsIgnoreCase(SSLD_TYPE)) {
            defaultConfiguration.add(new DeviceOutputSetting(1, 1, RelayType.LIGHT));
            defaultConfiguration.add(new DeviceOutputSetting(2, 2, RelayType.LIGHT));
            defaultConfiguration.add(new DeviceOutputSetting(3, 3, RelayType.TARIFF));
            return defaultConfiguration;
        }

        if (this.deviceType.equalsIgnoreCase(PSLD_TYPE)) {
            defaultConfiguration.add(new DeviceOutputSetting(1, 1, RelayType.LIGHT));
            return defaultConfiguration;
        }

        return defaultConfiguration;
    }
}
