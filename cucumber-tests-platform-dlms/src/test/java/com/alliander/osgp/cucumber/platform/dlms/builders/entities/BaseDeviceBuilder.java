/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.builders.entities;

import java.net.InetAddress;
import java.util.Date;
import java.util.Map;

import com.alliander.osgp.cucumber.platform.dlms.Defaults;
import com.alliander.osgp.cucumber.platform.dlms.Keys;
import com.alliander.osgp.cucumber.platform.inputparsers.DateInputParser;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.ProtocolInfo;

@SuppressWarnings("unchecked")
public abstract class BaseDeviceBuilder<T extends BaseDeviceBuilder<T>> {
    Long version = Defaults.VERSION;
    String deviceIdentification = Defaults.DEVICE_IDENTIFICATION;
    String deviceType = Defaults.DEVICE_TYPE;
    boolean isActivated = Defaults.IS_ACTIVATED;
    String containerCity = Defaults.CONTAINER_CITY;
    String containerStreet = Defaults.CONTAINER_STREET;
    Float gpsLatitude = Defaults.GPS_LATITUDE;
    Float gpsLongitude = Defaults.GPS_LONGITUDE;
    String containerPostalCode = Defaults.CONTAINER_POSTAL_CODE;
    String containerNumber = Defaults.CONTAINER_NUMBER;
    ProtocolInfo protocolInfo = null;
    InetAddress networkAddress = Defaults.NETWORK_ADDRESS;
    String containerMunicipality = Defaults.CONTAINER_MUNICIPALITY;
    String alias = Defaults.ALIAS;
    boolean inMaintenance = Defaults.IN_MAINTENANCE;
    Device gatewayDevice = Defaults.GATEWAY_DEVICE;
    Date technicalInstallationDate = Defaults.TECHNICAL_INSTALLATION_DATE;
    DeviceModel deviceModel = Defaults.DEVICE_MODEL;
    boolean isActive = Defaults.IS_ACTIVE;

    public T setVersion(final Long version) {
        this.version = version;
        return (T) this;
    }

    public T setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
        return (T) this;
    }

    public T setDeviceType(final String deviceType) {
        this.deviceType = deviceType;
        return (T) this;
    }

    public T setIsActivated(final boolean isActivated) {
        this.isActivated = isActivated;
        return (T) this;
    }

    public T setContainerCity(final String containerCity) {
        this.containerCity = containerCity;
        return (T) this;
    }

    public T setContainerStreet(final String containerStreet) {
        this.containerStreet = containerStreet;
        return (T) this;
    }

    public T setGpsLatitude(final Float gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
        return (T) this;
    }

    public T setGpsLongitude(final Float gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
        return (T) this;
    }

    public T setContainerPostalCode(final String containerPostalCode) {
        this.containerPostalCode = containerPostalCode;
        return (T) this;
    }

    public T setContainerNumber(final String containerNumber) {
        this.containerNumber = containerNumber;
        return (T) this;
    }

    public T setProtocolInfo(final ProtocolInfo protocolInfo) {
        this.protocolInfo = protocolInfo;
        return (T) this;
    }

    public T setNetworkAddress(final InetAddress networkAddress) {
        this.networkAddress = networkAddress;
        return (T) this;
    }

    public T setContainerMunicipality(final String containerMunicipality) {
        this.containerMunicipality = containerMunicipality;
        return (T) this;
    }

    public T setAlias(final String alias) {
        this.alias = alias;
        return (T) this;
    }

    public T setInMaintenance(final Boolean inMaintenance) {
        this.inMaintenance = inMaintenance;
        return (T) this;
    }

    public T setGatewayDevice(final Device gatewayDevice) {
        this.gatewayDevice = gatewayDevice;
        return (T) this;
    }

    public T setTechnicalInstallationDate(final Date technicalInstallationDate) {
        this.technicalInstallationDate = technicalInstallationDate;
        return (T) this;
    }

    public T setDeviceModel(final DeviceModel deviceModel) {
        this.deviceModel = deviceModel;
        return (T) this;
    }

    public T setIsActive(final boolean isActive) {
        this.isActive = isActive;
        return (T) this;
    }

    public T withSettings(final Map<String, String> inputSettings) {

        if (inputSettings.containsKey(Keys.VERSION)) {
            this.setVersion(Long.valueOf(inputSettings.get(Keys.VERSION)));
        }
        if (inputSettings.containsKey(Keys.DEVICE_IDENTIFICATION)) {
            this.setDeviceIdentification(inputSettings.get(Keys.DEVICE_IDENTIFICATION));
        }
        if (inputSettings.containsKey(Keys.DEVICE_TYPE)) {
            this.setDeviceType(inputSettings.get(Keys.DEVICE_TYPE));
        }
        if (inputSettings.containsKey(Keys.IS_ACTIVATED)) {
            this.setIsActivated(Boolean.parseBoolean(inputSettings.get(Keys.IS_ACTIVATED)));
        }
        if (inputSettings.containsKey(Keys.CONTAINER_CITY)) {
            this.setContainerCity(inputSettings.get(Keys.CONTAINER_CITY));
        }
        if (inputSettings.containsKey(Keys.CONTAINER_STREET)) {
            this.setContainerStreet(inputSettings.get(Keys.CONTAINER_STREET));
        }
        if (inputSettings.containsKey(Keys.GPS_LATITUDE)) {
            this.setGpsLatitude(Float.valueOf(inputSettings.get(Keys.GPS_LATITUDE)));
        }
        if (inputSettings.containsKey(Keys.GPS_LONGITUDE)) {
            this.setGpsLongitude(Float.valueOf(inputSettings.get(Keys.GPS_LONGITUDE)));
        }
        if (inputSettings.containsKey(Keys.CONTAINER_POSTAL_CODE)) {
            this.setContainerPostalCode(inputSettings.get(Keys.CONTAINER_POSTAL_CODE));
        }
        if (inputSettings.containsKey(Keys.CONTAINER_NUMBER)) {
            this.setContainerNumber(inputSettings.get(Keys.CONTAINER_NUMBER));
        }
        if (inputSettings.containsKey(Keys.CONTAINER_MUNICIPALITY)) {
            this.setContainerMunicipality(inputSettings.get(Keys.CONTAINER_MUNICIPALITY));
        }
        if (inputSettings.containsKey(Keys.ALIAS)) {
            this.setAlias(inputSettings.get(Keys.ALIAS));
        }
        if (inputSettings.containsKey(Keys.IN_MAINTENANCE)) {
            this.setInMaintenance(Boolean.parseBoolean(inputSettings.get(Keys.IN_MAINTENANCE)));
        }
        if (inputSettings.containsKey(Keys.TECHNICAL_INSTALLATION_DATE)) {
            this.setTechnicalInstallationDate(DateInputParser.parse(inputSettings.get(Keys.TECHNICAL_INSTALLATION_DATE)));
        }
        if (inputSettings.containsKey(Keys.IS_ACTIVE)) {
            this.setIsActive(Boolean.parseBoolean(inputSettings.get(Keys.IS_ACTIVE)));
        }
        return (T) this;
    }

}
