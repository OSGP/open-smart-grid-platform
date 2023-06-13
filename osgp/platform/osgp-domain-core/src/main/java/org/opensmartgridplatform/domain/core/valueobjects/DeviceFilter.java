// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects;

import java.util.List;

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
  private boolean exactMatch;
  private List<String> deviceIdentificationsToUse;
  private List<String> deviceIdentificationsToExclude;

  public DeviceFilter() {
    // Default constructor.
  }

  public String getOrganisationIdentification() {
    return this.organisationIdentification;
  }

  public void setOrganisationIdentification(final String organisationIdentification) {
    this.organisationIdentification = organisationIdentification;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public void setDeviceIdentification(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  public String getAlias() {
    return this.alias;
  }

  public void setAlias(final String alias) {
    this.alias = alias;
  }

  public String getCity() {
    return this.city;
  }

  public void setCity(final String city) {
    this.city = city;
  }

  public String getPostalCode() {
    return this.postalCode;
  }

  public void setPostalCode(final String postalCode) {
    this.postalCode = postalCode;
  }

  public String getStreet() {
    return this.street;
  }

  public void setStreet(final String street) {
    this.street = street;
  }

  public String getNumber() {
    return this.number;
  }

  public void setNumber(final String number) {
    this.number = number;
  }

  public String getMunicipality() {
    return this.municipality;
  }

  public void setMunicipality(final String municipality) {
    this.municipality = municipality;
  }

  public DeviceExternalManagedFilterType getDeviceExternalManaged() {
    return this.deviceExternalManaged;
  }

  public void setDeviceExternalManaged(
      final DeviceExternalManagedFilterType deviceExternalManaged) {
    this.deviceExternalManaged = deviceExternalManaged;
  }

  public DeviceActivatedFilterType getDeviceActivated() {
    return this.deviceActivated;
  }

  public void setDeviceActivated(final DeviceActivatedFilterType deviceActivated) {
    this.deviceActivated = deviceActivated;
  }

  public String getSortDir() {
    return this.sortDir;
  }

  public void setSortDir(final String sortDir) {
    this.sortDir = sortDir;
  }

  public String getSortedBy() {
    return this.sortedBy;
  }

  public void setSortedBy(final String sortedBy) {
    this.sortedBy = sortedBy;
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

  public boolean isExactMatch() {
    return this.exactMatch;
  }

  public void setExactMatch(final boolean exactMatch) {
    this.exactMatch = exactMatch;
  }

  public List<String> getDeviceIdentificationsToUse() {
    return this.deviceIdentificationsToUse;
  }

  public void setDeviceIdentificationsToUse(final List<String> deviceIdentificationsToUse) {
    this.deviceIdentificationsToUse = deviceIdentificationsToUse;
  }

  public List<String> getDeviceIdentificationsToExclude() {
    return this.deviceIdentificationsToExclude;
  }

  public void setDeviceIdentificationsToExclude(final List<String> deviceIdentificationsToExclude) {
    this.deviceIdentificationsToExclude = deviceIdentificationsToExclude;
  }
}
