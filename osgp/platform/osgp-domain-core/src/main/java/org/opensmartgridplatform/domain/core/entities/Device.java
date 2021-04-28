/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.entities;

import java.net.InetAddress;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import org.hibernate.annotations.SortNatural;
import org.hibernate.annotations.Type;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.CdmaSettings;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;
import org.opensmartgridplatform.domain.core.valueobjects.IntegrationType;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;
import org.opensmartgridplatform.shared.validation.Identification;

/**
 * Entity class which is the base for all smart devices. Other smart device entities should inherit
 * from this class. See {@link Ssld} / {@link SmartMeter} as examples.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Device extends AbstractEntity {

  /** Serial Version UID. */
  private static final long serialVersionUID = 10L;

  /** Device identification of a device. This is the main value used to find a device. */
  @Identification
  @Column(unique = true, nullable = false, length = 40, updatable = false)
  protected String deviceIdentification;

  /**
   * Alias of a device. Can be any String assigned to this device and can be used as alternate
   * identification.
   */
  @Column protected String alias;

  /** Address of a device */
  @Embedded
  @AttributeOverride(name = "city", column = @Column(name = "container_city"))
  @AttributeOverride(name = "street", column = @Column(name = "container_street"))
  @AttributeOverride(name = "postalCode", column = @Column(name = "container_postal_code"))
  @AttributeOverride(name = "number", column = @Column(name = "container_number"))
  @AttributeOverride(name = "numberAddition", column = @Column(name = "container_number_addition"))
  @AttributeOverride(name = "municipality", column = @Column(name = "container_municipality"))
  protected Address containerAddress;

  /** Gps information of a device */
  @Embedded
  @AttributeOverride(name = "latitude", column = @Column(name = "gps_latitude"))
  @AttributeOverride(name = "longitude", column = @Column(name = "gps_longitude"))
  protected GpsCoordinates gpsCoordinates;

  /** Cdma communication settings of a device */
  @Embedded protected CdmaSettings cdmaSettings;

  /** Indicates the type of the device. Example { @see Ssld.SSLD_TYPE } */
  protected String deviceType;

  /** IP address of a device. */
  @Column(length = 50)
  @Type(type = "org.opensmartgridplatform.shared.hibernate.InetAddressUserType")
  protected InetAddress networkAddress;

  /** Cell ID on a Base Transceiver Station. */
  @Column private Integer cellId;

  /** Base Transceiver Station ID. */
  @Column private Integer btsId;

  /**
   * Indicates if a device has been activated for the first time. This value is never updated after
   * the first time a device becomes active.
   */
  protected boolean isActivated;

  /**
   * List of { @see DeviceAuthorization.class } containing authorizations for this device. More that
   * one organisation can be authorized to use one ore more { @see DeviceFunctionGroup.class }.
   */
  @OneToMany(mappedBy = "device", targetEntity = DeviceAuthorization.class, fetch = FetchType.EAGER)
  private final List<DeviceAuthorization> authorizations = new ArrayList<>();

  /** Protocol information indicates which protocol this device is using. */
  @ManyToOne()
  @JoinColumn(name = "protocol_info_id")
  protected ProtocolInfo protocolInfo;

  /** Indicates if a device is in maintenance status. */
  @Column protected boolean inMaintenance;

  /** Gateway device through which communication with this device is handled. */
  @ManyToOne()
  @JoinColumn(name = "gateway_device_id")
  protected Device gatewayDevice;

  /** List of organisations which are authorized to use this device. */
  @Transient private final List<String> organisations = new ArrayList<>();

  @ManyToOne()
  @JoinColumn(name = "device_model")
  private DeviceModel deviceModel;

  /** Installation time of this entity. */
  @Column() protected Date technicalInstallationDate;

  /** DeviceLifecycleStatus of this entity */
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private DeviceLifecycleStatus deviceLifecycleStatus = DeviceLifecycleStatus.NEW_IN_INVENTORY;

  @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
  @SortNatural
  private final SortedSet<DeviceFirmwareFile> deviceFirmwareFiles = new TreeSet<>();

  @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
  @SortNatural
  private final SortedSet<DeviceFirmwareModule> deviceFirmwareModules = new TreeSet<>();

  @Column private Date lastSuccessfulConnectionTimestamp;

  @Column private Date lastFailedConnectionTimestamp;

  @Column(nullable = false)
  private Integer failedConnectionCount = 0;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private IntegrationType integrationType = IntegrationType.WEB_SERVICE;

  public Device() {
    // Default constructor
  }

  public Device(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  public Device(
      final String deviceIdentification,
      final String alias,
      final Address containerAddress,
      final GpsCoordinates gpsCoordinates,
      final CdmaSettings cdmaSettings) {
    this.deviceIdentification = deviceIdentification;
    this.alias = alias;
    this.containerAddress = containerAddress;
    this.gpsCoordinates = gpsCoordinates;
    this.cdmaSettings = cdmaSettings;
  }

  public DeviceAuthorization addAuthorization(
      final Organisation organisation, final DeviceFunctionGroup functionGroup) {
    final DeviceAuthorization authorization =
        new DeviceAuthorization(this, organisation, functionGroup);
    this.authorizations.add(authorization);
    return authorization;
  }

  public void removeAuthorization(
      final Organisation organisation, final DeviceFunctionGroup functionGroup) {
    for (final Iterator<DeviceAuthorization> iter = this.authorizations.listIterator();
        iter.hasNext(); ) {
      final DeviceAuthorization da = iter.next();
      if (da.getFunctionGroup().equals(functionGroup)
          && da.getOrganisation().equals(organisation)) {
        iter.remove();
      }
    }
  }

  public void addOrganisation(final String organisationIdentification) {
    if (!this.organisations.contains(organisationIdentification)) {
      this.organisations.add(organisationIdentification);
    }
  }

  public void clearNetworkAddress() {
    this.networkAddress = null;
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
    return Objects.equals(this.deviceIdentification, device.deviceIdentification);
  }

  public String getAlias() {
    return this.alias;
  }

  public List<DeviceAuthorization> getAuthorizations() {
    return this.authorizations;
  }

  public CdmaSettings getCdmaSettings() {
    return this.cdmaSettings;
  }

  public Address getContainerAddress() {
    return this.containerAddress;
  }

  public void setContainerAddress(final Address containerAddress) {
    this.containerAddress = containerAddress;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public GpsCoordinates getGpsCoordinates() {
    return this.gpsCoordinates;
  }

  public void setGpsCoordinates(final GpsCoordinates gpsCoordinates) {
    this.gpsCoordinates = gpsCoordinates;
  }

  public String getDeviceType() {
    return this.deviceType;
  }

  public void setDeviceType(final String deviceType) {
    this.deviceType = deviceType;
  }

  public String getIpAddress() {
    return this.networkAddress == null ? null : this.networkAddress.getHostAddress();
  }

  public InetAddress getNetworkAddress() {
    return this.networkAddress;
  }

  public void setNetworkAddress(final InetAddress networkAddress) {
    this.networkAddress = networkAddress;
  }

  public Integer getCellId() {
    return this.cellId;
  }

  public void setCellId(final Integer cellId) {
    this.cellId = cellId;
  }

  public Integer getBtsId() {
    return this.btsId;
  }

  public void setBtsId(final Integer btsId) {
    this.btsId = btsId;
  }

  /**
   * Get the organisations that are authorized for this device.
   *
   * @return List of OrganisationIdentification of organisations that are authorized for this
   *     device.
   */
  @Transient
  public List<String> getOrganisations() {
    return this.organisations;
  }

  /**
   * Get the owner organisation of the device.
   *
   * @return The organisation when an owner was set, null otherwise.
   */
  public Organisation getOwner() {
    for (final DeviceAuthorization authorization : this.authorizations) {
      if (authorization.getFunctionGroup().equals(DeviceFunctionGroup.OWNER)) {
        return authorization.getOrganisation();
      }
    }

    return null;
  }

  public ProtocolInfo getProtocolInfo() {
    return this.protocolInfo;
  }

  public Device getGatewayDevice() {
    return this.gatewayDevice;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.deviceIdentification);
  }

  public boolean isActivated() {
    return this.isActivated;
  }

  public void setActivated(final boolean isActivated) {
    this.isActivated = isActivated;
  }

  public boolean isInMaintenance() {
    return this.inMaintenance;
  }

  /**
   * This setter is only needed for testing. Don't use this in production code.
   *
   * @param id The id.
   */
  public void setId(final Long id) {
    this.id = id;
  }

  public void updateInMaintenance(final boolean inMaintenance) {
    this.inMaintenance = inMaintenance;
  }

  public void setAlias(final String alias) {
    this.alias = alias;
  }

  public void updateCdmaSettings(final CdmaSettings cdmaSettings) {
    this.cdmaSettings = cdmaSettings;
  }

  public void updateMetaData(
      final String alias, final Address address, final GpsCoordinates gpsCoordinates) {
    this.alias = alias;
    this.containerAddress = address;
    this.gpsCoordinates = gpsCoordinates;
  }

  public void updateProtocol(final ProtocolInfo protocolInfo) {
    this.protocolInfo = protocolInfo;
  }

  public void updateRegistrationData(final InetAddress networkAddress, final String deviceType) {
    this.networkAddress = networkAddress;
    this.deviceType = deviceType;
    this.isActivated = true;
    this.deviceLifecycleStatus = DeviceLifecycleStatus.IN_USE;
  }

  public void updateGatewayDevice(final Device gatewayDevice) {
    this.gatewayDevice = gatewayDevice;
  }

  public Date getTechnicalInstallationDate() {
    return this.technicalInstallationDate;
  }

  public void setTechnicalInstallationDate(final Date technicalInstallationDate) {
    this.technicalInstallationDate = technicalInstallationDate;
  }

  public DeviceModel getDeviceModel() {
    return this.deviceModel;
  }

  public void setDeviceModel(final DeviceModel deviceModel) {
    this.deviceModel = deviceModel;
  }

  public void addFirmwareFile(final FirmwareFile firmwareFile, final String installedBy) {
    final DeviceFirmwareFile newDeviceFirmware =
        new DeviceFirmwareFile(this, firmwareFile, new Date(), installedBy);
    this.deviceFirmwareFiles.add(newDeviceFirmware);
  }

  public FirmwareFile getActiveFirmwareFile() {
    /*
     * In general it could be the case that active firmware modules come
     * from different firmware files installed on the device.
     *
     * There used to be a simpler model where an attribute indicated which
     * single firmware was active for a device. For now this will return the
     * most recently installed firmware file.
     */
    if (this.deviceFirmwareFiles.isEmpty()) {
      return null;
    }
    return this.deviceFirmwareFiles.last().getFirmwareFile();
  }

  public Map<FirmwareModule, String> getFirmwareVersions() {
    return this.deviceFirmwareModules.stream()
        .collect(
            Collectors.toMap(
                DeviceFirmwareModule::getFirmwareModule, DeviceFirmwareModule::getModuleVersion));
  }

  public void setFirmwareVersions(final Map<FirmwareModule, String> firmwareVersions) {
    this.setFirmwareVersions(this.asDeviceFirmwareModules(firmwareVersions));
  }

  private Set<DeviceFirmwareModule> asDeviceFirmwareModules(
      final Map<FirmwareModule, String> firmwareVersions) {
    if (firmwareVersions == null) {
      return Collections.emptySet();
    }
    return firmwareVersions.entrySet().stream()
        .map(e -> new DeviceFirmwareModule(this, e.getKey(), e.getValue()))
        .collect(Collectors.toSet());
  }

  public void setFirmwareVersions(final Collection<DeviceFirmwareModule> deviceFirmwareModules) {
    if (deviceFirmwareModules == null) {
      this.setFirmwareVersions(Collections.emptyList());
      return;
    }

    this.clearDeviceFirmwareModules();
    deviceFirmwareModules.forEach(
        dfm -> {
          if (this.equals(dfm.getDevice())) {
            this.deviceFirmwareModules.add(dfm);
          }
        });
  }

  private void clearDeviceFirmwareModules() {
    final TreeSet<DeviceFirmwareModule> old = new TreeSet<>(this.deviceFirmwareModules);
    this.deviceFirmwareModules.clear();
    old.forEach(DeviceFirmwareModule::prepareForRemoval);
  }

  public DeviceLifecycleStatus getDeviceLifecycleStatus() {
    return this.deviceLifecycleStatus;
  }

  public void setDeviceLifecycleStatus(final DeviceLifecycleStatus deviceLifecycleStatus) {
    this.deviceLifecycleStatus = deviceLifecycleStatus;
  }

  public Date getLastSuccessfulConnectionTimestamp() {
    return this.lastSuccessfulConnectionTimestamp;
  }

  public void setLastSuccessfulConnectionTimestamp(final Date lastSuccessfulConnectionTimestamp) {
    this.lastSuccessfulConnectionTimestamp = lastSuccessfulConnectionTimestamp;
  }

  public void setLastSuccessfulConnectionTimestamp(
      final Instant lastSuccessfulConnectionTimestamp) {
    this.lastSuccessfulConnectionTimestamp = Date.from(lastSuccessfulConnectionTimestamp);
  }

  public Date getLastFailedConnectionTimestamp() {
    return this.lastFailedConnectionTimestamp;
  }

  public void setLastFailedConnectionTimestamp(final Date lastFailedConnectionTimestamp) {
    this.lastFailedConnectionTimestamp = lastFailedConnectionTimestamp;
  }

  public Integer getFailedConnectionCount() {
    return this.failedConnectionCount;
  }

  public void setFailedConnectionCount(final Integer failedConnectionCount) {
    this.failedConnectionCount = failedConnectionCount;
  }

  public void updateConnectionDetailsToSuccess() {
    this.failedConnectionCount = 0;
    this.lastSuccessfulConnectionTimestamp = new Date();
  }

  public void updateConnectionDetailsToFailure() {
    this.failedConnectionCount++;
    this.lastFailedConnectionTimestamp = new Date();
  }

  public boolean hasConnectionFailures() {
    return this.failedConnectionCount != 0;
  }

  public IntegrationType getIntegrationType() {
    return this.integrationType;
  }

  public void setIntegrationType(final IntegrationType integrationType) {
    this.integrationType = integrationType;
  }
}
