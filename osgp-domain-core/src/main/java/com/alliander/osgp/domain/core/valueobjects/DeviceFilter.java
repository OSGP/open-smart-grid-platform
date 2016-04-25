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

    public DeviceFilter() {

    }

    public DeviceFilter(final String organisationIdentification, final String deviceIdentification,
            final String street, final String city, final String postalCode, final String alias, final String number,
            final String municipality, final DeviceExternalManagedFilterType deviceExternalManaged,
            final DeviceActivatedFilterType deviceActivated,
            final DeviceInMaintenanceFilterType deviceInMaintenance, final String sortDir, final String sortedBy) {
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
}
