package com.alliander.osgp.domain.core.valueobjects;

public class DeviceFilter {

    private String organisationIdentification;
    private String deviceIdentification;
    private String city;
    private String postalCode;
    private String street;
    private String number;
    private String sortDir;
    private String sortedBy;

    public DeviceFilter() {

    }

    public DeviceFilter(final String organisationIdentification, final String deviceIdentification, final String city,
            final String postalCode, final String street, final String number, final String sortDir,
            final String sortedBy) {
        this.organisationIdentification = organisationIdentification;
        this.deviceIdentification = deviceIdentification;
        this.city = city;
        this.postalCode = postalCode;
        this.street = street;
        this.number = number;
        this.sortDir = sortDir;
        this.sortedBy = sortedBy;
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
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

    public String getSortDir() {
        return this.sortDir;
    }

    public String getSortedBy() {
        return this.sortedBy;
    }
}
