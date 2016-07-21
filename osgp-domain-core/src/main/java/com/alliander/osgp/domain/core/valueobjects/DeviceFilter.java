/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects;

public class DeviceFilter {

    private String organisationIdentification;
    private String alias;
    private String deviceIdentification;
    private String city;
    private String postalCode;
    private String street;
    private String number;
    private String municipality;
    private DeviceExternalManagedFilterType deviceExternalManaged;
    private DeviceActivatedFilterType deviceActivated;
    private DeviceInMaintenanceFilterType deviceInMaintenance;
    private String sortDir;
    private String sortedBy;
    private boolean hasTechnicalInstallation;
    private String owner;
    private String deviceType;
    private String manufacturer;
    private String model;
    private FirmwareModuleFilterType firmwareModuleType;
    private String firmwareModuleVersion;

    public DeviceFilter() {
        // Default constructor.
    }

    public DeviceFilter(final String organisationIdentification, final String deviceIdentification,
            final String street, final String city, final String postalCode, final String alias, final String number,
            final String municipality, final DeviceExternalManagedFilterType deviceExternalManaged,
            final DeviceActivatedFilterType deviceActivated, final DeviceInMaintenanceFilterType deviceInMaintenance,
            final String sortDir, final String sortedBy, final boolean hasTechnicalInstallation, final String owner,
            final String deviceType, final String manufacturer, final String model,
            final FirmwareModuleFilterType firmwareModuleType, final String firmwareModuleVersion) {
        this.organisationIdentification = organisationIdentification;
        this.deviceIdentification = deviceIdentification;
        this.alias = alias;
        this.city = city;
        this.postalCode = postalCode;
        this.street = street;
        this.number = number;
        this.municipality = municipality;
        this.deviceExternalManaged = deviceExternalManaged;
        this.deviceActivated = deviceActivated;
        this.deviceInMaintenance = deviceInMaintenance;
        this.sortDir = sortDir;
        this.sortedBy = sortedBy;
        this.hasTechnicalInstallation = hasTechnicalInstallation;
        this.owner = owner;
        this.deviceType = deviceType;
        this.manufacturer = manufacturer;
        this.model = model;
        this.firmwareModuleType = firmwareModuleType;
        this.firmwareModuleVersion = firmwareModuleVersion;
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public String getAlias() {
        return this.alias;
    }

    public String getCity() {
        return this.city;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public String getStreet() {
        return this.street;
    }

    public String getNumber() {
        return this.number;
    }

    public String getMunicipality() {
        return this.municipality;
    }

    public DeviceExternalManagedFilterType getDeviceExternalManaged() {
        return this.deviceExternalManaged;
    }

    public DeviceActivatedFilterType getDeviceActivated() {
        return this.deviceActivated;
    }

    public String getSortDir() {
        return this.sortDir;
    }

    public String getSortedBy() {
        return this.sortedBy;
    }

    public void updateOrganisationIdentification(final String organisationIdentification) {
        this.organisationIdentification = organisationIdentification;
    }

    public DeviceInMaintenanceFilterType getDeviceInMaintenance() {
        return this.deviceInMaintenance;
    }

    public void setDeviceInMaintenance(final DeviceInMaintenanceFilterType deviceInMaintenance) {
        this.deviceInMaintenance = deviceInMaintenance;
    }

    public boolean isHasTechnicalInstallation() {
        return this.hasTechnicalInstallation;
    }

    public void setHasTechnicalInstallation(final boolean hasTechnicalInstallation) {
        this.hasTechnicalInstallation = hasTechnicalInstallation;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public String getDeviceType() {
        return this.deviceType;
    }

    public void setDeviceType(final String deviceType) {
        this.deviceType = deviceType;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public void setManufacturer(final String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(final String model) {
        this.model = model;
    }

    public FirmwareModuleFilterType getFirmwareModuleType() {
        return this.firmwareModuleType;
    }

    public void setFirmwareModuleType(final FirmwareModuleFilterType firmwareModuleType) {
        this.firmwareModuleType = firmwareModuleType;
    }

    public String getFirmwareModuleVersion() {
        return this.firmwareModuleVersion;
    }

    public void setFirmwareModuleVersion(final String firmwareModuleVersion) {
        this.firmwareModuleVersion = firmwareModuleVersion;
    }
}
