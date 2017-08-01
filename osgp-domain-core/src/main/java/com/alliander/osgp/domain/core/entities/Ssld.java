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
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.alliander.osgp.domain.core.valueobjects.RelayType;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Ssld extends Device {
    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 1492794916633445439L;

    /**
     * Device type indicator for PSLD
     */
    public static final String PSLD_TYPE = "PSLD";

    /**
     * Device type indicator for SSLD
     */
    public static final String SSLD_TYPE = "SSLD";

    @Column()
    private boolean hasPublicKey;

    private boolean hasSchedule;

    @OneToMany(mappedBy = "device", targetEntity = Ean.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Ean> eans = new ArrayList<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ElementCollection()
    @CollectionTable(name = "device_output_setting", joinColumns = @JoinColumn(name = "device_id"))
    private List<DeviceOutputSetting> outputSettings = new ArrayList<>();

    @OneToMany(mappedBy = "device", cascade = { CascadeType.MERGE, CascadeType.PERSIST }, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<RelayStatus> relayStatusses;

    @ManyToOne(optional = true, cascade = { CascadeType.MERGE, CascadeType.PERSIST }, fetch = FetchType.LAZY)
    @JoinColumn(name = "light_measurement_device_id")
    private LightMeasurementDevice lightMeasurementDevice;

    public Ssld() {
        // Default constructor.
    }

    public Ssld(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public Ssld(final String deviceIdentification, final String deviceType, final InetAddress networkAddress,
            final boolean activated, final boolean hasSchedule) {
        this.deviceIdentification = deviceIdentification;
        this.deviceType = deviceType;
        this.networkAddress = networkAddress;
        this.isActivated = activated;
        this.hasSchedule = hasSchedule;
    }

    public Ssld(final String deviceIdentification, final String alias, final String containerCity,
            final String containerPostalCode, final String containerStreet, final String containerNumber,
            final String containerMunicipality, final Float latitude, final Float longitude) {
        this.deviceIdentification = deviceIdentification;
        this.alias = alias;
        this.containerCity = containerCity;
        this.containerPostalCode = containerPostalCode;
        this.containerStreet = containerStreet;
        this.containerNumber = containerNumber;
        this.containerMunicipality = containerMunicipality;
        this.gpsLatitude = latitude;
        this.gpsLongitude = longitude;
    }

    public boolean isPublicKeyPresent() {
        return this.hasPublicKey;
    }

    public void setPublicKeyPresent(final boolean isPublicKeyPresent) {
        this.hasPublicKey = isPublicKeyPresent;
    }

    public void setHasSchedule(final boolean hasSchedule) {
        this.hasSchedule = hasSchedule;
    }

    public boolean getHasSchedule() {
        return this.hasSchedule;
    }

    /**
     * Get the Ean codes for this device.
     *
     * @return List of Ean codes for this device.
     */
    public List<Ean> getEans() {
        return this.eans;
    }

    public void setEans(final List<Ean> eans) {
        this.eans.clear();
        if (eans != null) {
            this.eans.addAll(eans);
        }
    }

    public void addEan(final Ean ean) {
        this.eans.add(ean);
    }

    public List<DeviceOutputSetting> getOutputSettings() {
        if (this.outputSettings == null || this.outputSettings.isEmpty()) {
            return Collections.unmodifiableList(this.createDefaultConfiguration());
        }

        return Collections.unmodifiableList(this.outputSettings);
    }

    public void updateOutputSettings(final List<DeviceOutputSetting> outputSettings) {
        this.outputSettings = outputSettings;
    }

    public List<DeviceOutputSetting> receiveOutputSettings() {
        return this.outputSettings;
    }

    public List<RelayStatus> getRelayStatusses() {
        return this.relayStatusses;
    }

    public void setRelayStatusses(final List<RelayStatus> relayStatusses) {
        this.relayStatusses = relayStatusses;
    }

    /**
     * Returns the {@link RelayStatus} for the given index, or null if it
     * doesn't exist.
     */
    public RelayStatus getRelayStatusByIndex(final int index) {
        if (this.relayStatusses != null) {
            for (final RelayStatus r : this.relayStatusses) {
                if (r.getIndex() == index) {
                    return r;
                }
            }
        }
        return null;
    }

    public void setLightMeasurementDevice(final LightMeasurementDevice lightMeasurementDevice) {
        this.lightMeasurementDevice = lightMeasurementDevice;
    }

    public LightMeasurementDevice getLightMeasurementDevice() {
        return this.lightMeasurementDevice;
    }

    /**
     * Updates the {@link RelayStatus} for the given index if it exists.
     */
    public void updateRelayStatusByIndex(final int index, final RelayStatus relayStatus) {

        boolean found = false;
        if (this.relayStatusses != null) {
            for (final RelayStatus r : this.relayStatusses) {
                if (r.getIndex() == index) {
                    r.updateStatus(relayStatus.isLastKnownState(), relayStatus.getLastKnowSwitchingTime());
                    found = true;
                    break;
                }
            }

            if (!found) {
                this.relayStatusses.add(relayStatus);
            }
        }
    }

    public void updateRelayStatusses(final Map<Integer, RelayStatus> relayStatusByIndex) {
        if (this.relayStatusses == null) {
            this.relayStatusses = new ArrayList<>();
        }
        final Map<Integer, RelayStatus> unhandledStatussesByIndex = new TreeMap<>(relayStatusByIndex);
        for (final RelayStatus r : this.relayStatusses) {
            final RelayStatus newStatus = unhandledStatussesByIndex.remove(r.getIndex());
            if (newStatus != null && newStatus.getLastKnowSwitchingTime().after(r.getLastKnowSwitchingTime())) {
                r.updateStatus(newStatus.isLastKnownState(), newStatus.getLastKnowSwitchingTime());
            }
        }
        this.relayStatusses.addAll(unhandledStatussesByIndex.values());
    }

    /**
     * Create default configuration for a device (based on type).
     *
     * @return default configuration
     */
    private List<DeviceOutputSetting> createDefaultConfiguration() {
        final List<DeviceOutputSetting> defaultConfiguration = new ArrayList<>();

        if (this.deviceType == null) {
            return defaultConfiguration;
        }

        if (this.deviceType.equalsIgnoreCase(SSLD_TYPE)) {
            defaultConfiguration.add(new DeviceOutputSetting(1, 1, RelayType.TARIFF, ""));
            defaultConfiguration.add(new DeviceOutputSetting(2, 2, RelayType.LIGHT, ""));
            defaultConfiguration.add(new DeviceOutputSetting(3, 3, RelayType.LIGHT, ""));

            return defaultConfiguration;
        }

        if (this.deviceType.equalsIgnoreCase(PSLD_TYPE)) {
            defaultConfiguration.add(new DeviceOutputSetting(1, 1, RelayType.LIGHT, ""));
            return defaultConfiguration;
        }

        return defaultConfiguration;
    }
}
