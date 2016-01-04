/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
import com.alliander.osgp.shared.domain.entities.AbstractEntity;

@Entity
public class SmartMeteringDevice extends AbstractEntity implements DeviceInterface, LocationInformationInterface {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -3526823976188640681L;

    @Identification
    @Column(unique = true, nullable = false, length = 40)
    private String deviceIdentification;

    @Column
    private String deviceType;

    @Column
    private String supplier;

    @ManyToOne()
    @JoinColumn(name = "protocol_info_id")
    private ProtocolInfo protocolInfo;

    // TODO potential future fields
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

    @Transient
    private final List<DeviceAuthorization> authorizations = new ArrayList<DeviceAuthorization>();

    @Transient
    private final List<String> organisations = new ArrayList<String>();

    public SmartMeteringDevice() {
        // Default constructor for hibernate
    }

    /*
     * (non-Javadoc)
     *
     * @see com.alliander.osgp.domain.core.entities.DeviceInterface#
     * getDeviceIdentification()
     */
    @Override
    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.alliander.osgp.domain.core.entities.DeviceInterface#
     * getContainerPostalCode()
     */
    @Override
    public String getContainerPostalCode() {
        return this.containerPostalCode;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     *
     * com.alliander.osgp.domain.core.entities.DeviceInterface#getContainerCity
     * ()
     */
    @Override
    public String getContainerCity() {
        return this.containerCity;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     *
     * com.alliander.osgp.domain.core.entities.DeviceInterface#getContainerStreet
     * ()
     */
    @Override
    public String getContainerStreet() {
        return this.containerStreet;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     *
     * com.alliander.osgp.domain.core.entities.DeviceInterface#getContainerNumber
     * ()
     */
    @Override
    public String getContainerNumber() {
        return this.containerNumber;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     *
     * com.alliander.osgp.domain.core.entities.DeviceInterface#getGpsLatitude()
     */
    @Override
    public Float getGpsLatitude() {
        return this.gpsLatitude;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     *
     * com.alliander.osgp.domain.core.entities.DeviceInterface#getGpsLongitude()
     */
    @Override
    public Float getGpsLongitude() {
        return this.gpsLongitude;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.alliander.osgp.domain.core.entities.DeviceInterface#getDeviceType()
     */
    @Override
    public String getDeviceType() {
        return this.deviceType;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     *
     * com.alliander.osgp.domain.core.entities.DeviceInterface#getProtocolInfo()
     */
    @Override
    public ProtocolInfo getProtocolInfo() {
        return this.protocolInfo;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     *
     * com.alliander.osgp.domain.core.entities.DeviceInterface#getAuthorizations
     * ()
     */
    @Override
    public List<DeviceAuthorization> getAuthorizations() {
        return this.authorizations;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     *
     * com.alliander.osgp.domain.core.entities.DeviceInterface#addAuthorization
     * (com.alliander.osgp.domain.core.entities.Organisation,
     * com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup)
     */
    @Override
    public DeviceAuthorization addAuthorization(final Organisation organisation, final DeviceFunctionGroup functionGroup) {

        // final DeviceAuthorization authorization = new
        // DeviceAuthorization(this.device, organisation, functionGroup);
        // this.authorizations.add(authorization);
        // return authorization;

        System.out.println("NOT IMPLEMENTED: SmartMeteringDevice.addAutorization(), returning NULL");
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.alliander.osgp.domain.core.entities.DeviceInterface#getOwner()
     */
    @Override
    public Organisation getOwner() {
        if (this.authorizations != null) {
            for (final DeviceAuthorization authorization : this.authorizations) {
                if (authorization.getFunctionGroup().equals(DeviceFunctionGroup.OWNER)) {
                    authorization.getOrganisation();
                }
            }
        }

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     *
     * com.alliander.osgp.domain.core.entities.DeviceInterface#getOrganisations
     * ()
     */
    @Override
    public List<String> getOrganisations() {
        return this.organisations;
    }

    public void updateProtocol(final ProtocolInfo protocolInfo) {
        this.protocolInfo = protocolInfo;
    }

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
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

}
