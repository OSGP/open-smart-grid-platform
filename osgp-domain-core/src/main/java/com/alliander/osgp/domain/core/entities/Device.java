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
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Type;

import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Device {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -1067112091560627041L;

    /**
     * Device type indicator for PSLD
     */
    public static final String PSLD_TYPE = "PSLD";

    /**
     * Device type indicator for SSLD
     */
    public static final String SSLD_TYPE = "SSLD";

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "id")
    protected Long id;

    @Column(nullable = false)
    private Date creationTime = new Date();

    @Column(nullable = false)
    private Date modificationTime = new Date();

    @Version
    private Long version = -1L;

    public final Long getId() {
        return this.id;
    }

    public final Date getCreationTime() {
        return (Date) this.creationTime.clone();
    }

    public final Date getModificationTime() {
        return (Date) this.modificationTime.clone();
    }

    public final Long getVersion() {
        return this.version;
    }

    public void setVersion(final Long newVersion) {
        this.version = newVersion;
    }

    /**
     * Method for actions to be taken before inserting.
     */
    @PrePersist
    private void prePersist() {
        final Date now = new Date();
        this.creationTime = now;
        this.modificationTime = now;
    }

    /**
     * Method for actions to be taken before updating.
     */
    @PreUpdate
    private void preUpdate() {
        this.modificationTime = new Date();
    }

    @Identification
    @Column(unique = true, nullable = false, length = 40)
    protected String deviceIdentification;

    @Column
    protected String alias;

    @Column(length = 255)
    protected String containerCity;
    @Column(length = 255)
    protected String containerStreet;
    @Column(length = 10)
    protected String containerPostalCode;
    @Column(length = 255)
    protected String containerNumber;

    @Column(length = 255)
    protected String containerMunicipality;

    @Column
    protected Float gpsLatitude;
    @Column
    protected Float gpsLongitude;

    protected String deviceType;

    @Column(length = 50)
    @Type(type = "com.alliander.osgp.shared.hibernate.InetAddressUserType")
    protected InetAddress networkAddress;

    protected boolean isActivated;

    @OneToMany(mappedBy = "device", targetEntity = DeviceAuthorization.class, fetch = FetchType.EAGER)
    private final List<DeviceAuthorization> authorizations = new ArrayList<DeviceAuthorization>();

    // @LazyCollection(LazyCollectionOption.FALSE)
    // @ElementCollection()
    // @CollectionTable(name = "device_output_setting", joinColumns =
    // @JoinColumn(name = "device_id"))
    // private List<DeviceOutputSetting> outputSettings = new ArrayList<>();

    // private boolean hasSchedule;

    @Transient
    private final List<String> organisations = new ArrayList<String>();

    @ManyToOne()
    @JoinColumn(name = "protocol_info_id")
    private ProtocolInfo protocolInfo;

    // @OneToMany(mappedBy = "device", targetEntity = Ean.class)
    // @LazyCollection(LazyCollectionOption.FALSE)
    // private final List<Ean> eans = new ArrayList<Ean>();

    // @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    // @LazyCollection(LazyCollectionOption.FALSE)
    // private List<RelayStatus> relayStatusses;

    @Column
    private boolean inMaintenance;

    public Device() {
        // Default constructor
    }

    // public Device(final String deviceIdentification, final String deviceType,
    // final InetAddress networkAddress,
    // final boolean activated, final boolean hasSchedule) {
    // this.deviceIdentification = deviceIdentification;
    // this.deviceType = deviceType;
    // this.networkAddress = networkAddress;
    // this.isActivated = activated;
    // this.hasSchedule = hasSchedule;
    // }

    public Device(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public Device(final String deviceIdentification, final String alias, final String containerCity,
            final String containerPostalCode, final String containerStreet, final String containerNumber,
            final String containerMunicipality, final Float gpsLatitude, final Float gpsLongitude) {
        this.deviceIdentification = deviceIdentification;
        this.alias = alias;
        this.containerCity = containerCity;
        this.containerPostalCode = containerPostalCode;
        this.containerStreet = containerStreet;
        this.containerNumber = containerNumber;
        this.containerMunicipality = containerMunicipality;
        this.gpsLatitude = gpsLatitude;
        this.gpsLongitude = gpsLongitude;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public String getAlias() {
        return this.alias;
    }

    public String getContainerPostalCode() {
        return this.containerPostalCode;
    }

    public String getContainerCity() {
        return this.containerCity;
    }

    public String getContainerStreet() {
        return this.containerStreet;
    }

    public String getContainerNumber() {
        return this.containerNumber;
    }

    public String getContainerMunicipality() {
        return this.containerMunicipality;
    }

    public Float getGpsLatitude() {
        return this.gpsLatitude;
    }

    public Float getGpsLongitude() {
        return this.gpsLongitude;
    }

    public String getDeviceType() {
        return this.deviceType;
    }

    public InetAddress getNetworkAddress() {
        return this.networkAddress;
    }

    public String getIpAddress() {
        return this.networkAddress == null ? null : this.networkAddress.getHostAddress();
    }

    public boolean isActivated() {
        return this.isActivated;
    }

    // public boolean getHasSchedule() {
    // return this.hasSchedule;
    // }

    // public boolean isPublicKeyPresent() {
    // return this.hasPublicKey;
    // }
    //
    // public void setPublicKeyPresent(final boolean isPublicKeyPresent) {
    // this.hasPublicKey = isPublicKeyPresent;
    // }

    public ProtocolInfo getProtocolInfo() {
        return this.protocolInfo;
    }

    public boolean isInMaintenance() {
        return this.inMaintenance;
    }

    public void updateMetaData(final String alias, final String containerCity, final String containerPostalCode,
            final String containerStreet, final String containerNumber, final String containerMunicipality,
            final Float gpsLatitude, final Float gpsLongitude) {
        this.alias = alias;
        this.containerCity = containerCity;
        this.containerPostalCode = containerPostalCode;
        this.containerStreet = containerStreet;
        this.containerNumber = containerNumber;
        this.containerMunicipality = containerMunicipality;
        this.gpsLatitude = gpsLatitude;
        this.gpsLongitude = gpsLongitude;

    }

    public void updateRegistrationData(final InetAddress networkAddress, final String deviceType) {
        // Set the incoming values.
        this.networkAddress = networkAddress;
        this.deviceType = deviceType;
        this.isActivated = true;
    }

    // public void updateOutputSettings(final List<DeviceOutputSetting>
    // outputSettings) {
    // this.outputSettings = outputSettings;
    // }

    public void updateProtocol(final ProtocolInfo protocolInfo) {
        this.protocolInfo = protocolInfo;
    }

    public void updateInMaintenance(final boolean inMaintenance) {
        this.inMaintenance = inMaintenance;
    }

    public void clearNetworkAddress() {
        this.networkAddress = null;
    }

    public List<DeviceAuthorization> getAuthorizations() {
        return this.authorizations;
    }

    // public List<DeviceOutputSetting> getOutputSettings() {
    // if (this.outputSettings == null || this.outputSettings.isEmpty()) {
    // return Collections.unmodifiableList(this.createDefaultConfiguration());
    // }
    //
    // return Collections.unmodifiableList(this.outputSettings);
    // }
    //
    // public List<DeviceOutputSetting> receiveOutputSettings() {
    // return this.outputSettings;
    // }

    /**
     * Get the owner organisation of the device.
     *
     * @return The organisation when an owner was set, null otherwise.
     */
    public Organisation getOwner() {
        if (this.authorizations != null) {
            for (final DeviceAuthorization authorization : this.authorizations) {
                if (authorization.getFunctionGroup().equals(DeviceFunctionGroup.OWNER)) {
                    return authorization.getOrganisation();
                }
            }
        }

        return null;
    }

    // public List<RelayStatus> getRelayStatusses() {
    // return this.relayStatusses;
    // }
    //
    // /**
    // * Returns the {@link RelayStatus} for the given index, or null if it
    // * doesn't exist.
    // */
    // public RelayStatus getRelayStatusByIndex(final int index) {
    // if (this.relayStatusses != null) {
    // for (final RelayStatus r : this.relayStatusses) {
    // if (r.getIndex() == index) {
    // return r;
    // }
    // }
    // }
    // return null;
    // }
    //
    // /**
    // * Updates the {@link RelayStatus} for the given index if it exists.
    // */
    // public void updateRelayStatusByIndex(final int index, final RelayStatus
    // relayStatus) {
    //
    // boolean found = false;
    // if (this.relayStatusses != null) {
    // for (final RelayStatus r : this.relayStatusses) {
    // if (r.getIndex() == index) {
    // r.updateStatus(relayStatus.isLastKnownState(),
    // relayStatus.getLastKnowSwitchingTime());
    // found = true;
    // break;
    // }
    // }
    //
    // if (!found) {
    // this.relayStatusses.add(relayStatus);
    // }
    // }
    // }

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
        // if (this.hasSchedule != device.hasSchedule) {
        // return false;
        // }
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
        // if (this.eans != null ? !this.eans.equals(device.eans) : device.eans
        // != null) {
        // return false;
        // }
        return true;
    }

    @Override
    public int hashCode() {
        int result = this.deviceIdentification != null ? this.deviceIdentification.hashCode() : 0;
        result = 31 * result + (this.deviceType != null ? this.deviceType.hashCode() : 0);
        result = 31 * result + (this.networkAddress != null ? this.networkAddress.hashCode() : 0);
        result = 31 * result + (this.isActivated ? 1 : 0);
        // result = 31 * result + (this.hasSchedule ? 1 : 0);
        result = 31 * result + (this.authorizations != null ? this.authorizations.hashCode() : 0);
        // result = 31 * result + (this.eans != null ? this.eans.hashCode() :
        // 0);
        return result;
    }

    // public void setHasSchedule(final boolean hasSchedule) {
    // this.hasSchedule = hasSchedule;
    // }

    /**
     * Get the organisations that are authorized for this device.
     *
     * @return List of OrganisationIdentification of organisations that are
     *         authorized for this device.
     */

    @Transient
    public List<String> getOrganisations() {
        return this.organisations;
    }

    /**
     * Get the Ean codes for this device.
     *
     * @return List of Ean codes for this device.
     */
    // public List<Ean> getEans() {
    // return this.eans;
    // }

    public void addOrganisation(final String organisationIdentification) {
        this.organisations.add(organisationIdentification);
    }

    /**
     * Create default configuration for a device (based on type).
     *
     * @return default configuration
     */
    // private List<DeviceOutputSetting> createDefaultConfiguration() {
    // final List<DeviceOutputSetting> defaultConfiguration = new ArrayList<>();
    //
    // if (this.deviceType == null) {
    // return defaultConfiguration;
    // }
    //
    // if (this.deviceType.equalsIgnoreCase(SSLD_TYPE)) {
    // defaultConfiguration.add(new DeviceOutputSetting(1, 1, RelayType.LIGHT,
    // ""));
    // defaultConfiguration.add(new DeviceOutputSetting(2, 2, RelayType.LIGHT,
    // ""));
    // defaultConfiguration.add(new DeviceOutputSetting(3, 3, RelayType.TARIFF,
    // ""));
    //
    // return defaultConfiguration;
    // }
    //
    // if (this.deviceType.equalsIgnoreCase(PSLD_TYPE)) {
    // defaultConfiguration.add(new DeviceOutputSetting(1, 1, RelayType.LIGHT,
    // ""));
    // return defaultConfiguration;
    // }
    //
    // return defaultConfiguration;
    // }
}
