/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.database.device;

import java.net.InetAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Type;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceFirmware;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.ProtocolInfo;
import com.alliander.osgp.platform.dlms.cucumber.steps.Defaults;
import com.alliander.osgp.platform.dlms.cucumber.steps.Keys;

public class DeviceBuilder implements Builder<Device>{

    private Long version = Defaults.DEFAULT_VERSION;
    private String deviceIdentification = Defaults.DEFAULT_DEVICE_IDENTIFICATION;
    private String deviceType = Defaults.DEFAULT_DEVICE_TYPE;
    private boolean isActivated = Defaults.DEFAULT_IS_ACTIVATED;   
    private String containerCity = Defaults.DEFAULT_CONTAINER_CITY;
    private String containerStreet = Defaults.DEFAULT_CONTAINER_STREET;
    private Float gpsLatitude = Defaults.DEFAULT_GPS_LATITUDE;
    private Float gpsLongitude = Defaults.DEFAULT_GPS_LONGITUDE;
    private String containerPostalCode = Defaults.DEFAULT_CONTAINER_POSTAL_CODE;
    private String containerNumber = Defaults.DEFAULT_CONTAINER_NUMBER;
    private Long protocolInfo = Defaults.DEFAULT_PROTOCOL_INFO;
    private String networkAddress = Defaults.DEFAULT_NETWORK_ADDRESS;
    private String containerMunicipality = Defaults.DEFAULT_CONTAINER_MUNICIPALITY;
    private String alias = Defaults.DEFAULT_ALIAS;
    private boolean inMaintenance = Defaults.DEFAULT_IN_MAINTENANCE;
    private Long gatewayDevice = Defaults.DEFAULT_GATEWAY_DEVICE;
    private Date technicalInstallationDate = Defaults.DEFAULT_TECHNICAL_INSTALLATION_DATE;
    private Long deviceModel = Defaults.DEFAULT_DEVICE_MODEL;
    private boolean isActive = Defaults.DEFAULT_IS_ACTIVE;
           
    private Long organisation = null;
    private Long functionGroup = null;

    public DeviceBuilder setVersion(final Long version) {
        this.version = version;
        return this;
    }
    
    public DeviceBuilder setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
        return this;
    }
    
    public DeviceBuilder setDeviceType(final String deviceType) {
        this.deviceType = deviceType;
        return this;
    }
    
    public DeviceBuilder setIsActivated(final boolean isActivated) {
        this.isActivated = isActivated;
        return this;
    }
    
    public DeviceBuilder setContainerCity(final String containerCity) {
        this.containerCity = containerCity;
        return this;
    }
    
    public DeviceBuilder setContainerStreet(final String containerStreet) {
        this.containerStreet = containerStreet;
        return this;
    }
    
    public DeviceBuilder setGpsLatitude(final Float gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
        return this;
    }
    
    public DeviceBuilder setGpsLongitude(final Float gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
        return this;
    }
    
    public DeviceBuilder setContainerPostalCode(String containerPostalCode) {
        this.containerPostalCode = containerPostalCode;
        return this;
    }
    
    public DeviceBuilder setContainerNumber(String containerNumber) {
        this.containerNumber = containerNumber;
        return this;
    }
    
    public DeviceBuilder setProtocolInfo(Long protocolInfo) {
        this.protocolInfo = protocolInfo;
        return this;
    }
    
    public DeviceBuilder setNetworkAddress(String networkAddress) {
        this.networkAddress = networkAddress;
        return this;
    }
    
    public DeviceBuilder setContainerMunicipality(String containerMunicipality) {
        this.containerMunicipality = containerMunicipality;
        return this;
    }
    
    public DeviceBuilder setAlias(String alias) {
        this.alias = alias;
        return this;
    }
    
    public DeviceBuilder setInMaintenance(Boolean inMaintenance) {
        this.inMaintenance = inMaintenance;
        return this;
    }
    
    public DeviceBuilder setGatewayDevice(Long gatewayDevice) {
        this.gatewayDevice = gatewayDevice;
        return this;
    }
    
    public DeviceBuilder setTechnicalInstallationDate(Date technicalInstallationDate) {
        this.technicalInstallationDate = technicalInstallationDate;
        return this;
    }
    
    public DeviceBuilder setDeviceModel(Long deviceModel) {
        this.deviceModel = deviceModel;
        return this;
    }
    
    public DeviceBuilder setIsActive(boolean isActive) {
        this.isActive = isActive;
        return this;
    }
    
    
    
    
    
    
    
    public DeviceBuilder setOrganisationId(final Long organisation) {
        this.organisation = organisation;
        return this;
    }
    
    
    public DeviceBuilder buildDevice(final Map<String, String> inputSettings) {

        if (inputSettings.containsKey(Keys.KEY_VERSION)) {
            this.setVersion(Long.parseLong(inputSettings.get(Keys.KEY_VERSION)));
        }
        if (inputSettings.containsKey(Keys.KEY_DEVICE_IDENTIFICATION)) {
            this.setDeviceIdentification(inputSettings.get(Keys.KEY_DEVICE_IDENTIFICATION));
        }
        if (inputSettings.containsKey(Keys.KEY_DEVICE_TYPE)) {
            this.setDeviceType(inputSettings.get(Keys.KEY_DEVICE_TYPE));
        }
        if (inputSettings.containsKey(Keys.KEY_IS_ACTIVATED)) {
            this.setIsActivated(Boolean.parseBoolean(inputSettings.get(Keys.KEY_IS_ACTIVATED)));
        }
        if (inputSettings.containsKey(Keys.KEY_CONTAINER_CITY)) {
            this.setContainerCity(inputSettings.get(Keys.KEY_CONTAINER_CITY));
        }
        if (inputSettings.containsKey(Keys.KEY_CONTAINER_STREET)) {
            this.setContainerStreet(inputSettings.get(Keys.KEY_CONTAINER_STREET));
        }
        if (inputSettings.containsKey(Keys.KEY_GPS_LATITUDE)) {
            this.setGpsLatitude(Float.parseFloat(inputSettings.get(Keys.KEY_GPS_LATITUDE)));
        }
        if (inputSettings.containsKey(Keys.KEY_GPS_LONGITUDE)) {
            this.setGpsLongitude(Float.parseFloat(inputSettings.get(Keys.KEY_GPS_LONGITUDE)));
        }
        if (inputSettings.containsKey(Keys.KEY_CONTAINER_POSTAL_CODE)) {
            this.setContainerPostalCode(inputSettings.get(Keys.KEY_CONTAINER_POSTAL_CODE));
        }
        if (inputSettings.containsKey(Keys.KEY_CONTAINER_NUMBER)) {
            this.setContainerNumber(inputSettings.get(Keys.KEY_CONTAINER_NUMBER));
        }
        if (inputSettings.containsKey(Keys.KEY_PROTOCOL_INFO)) {
            this.setProtocolInfo(Long.parseLong(inputSettings.get(Keys.KEY_PROTOCOL_INFO)));
        }
        if (inputSettings.containsKey(Keys.KEY_NETWORK_ADDRESS)) {
            this.setNetworkAddress(inputSettings.get(Keys.KEY_NETWORK_ADDRESS));
        }
        if (inputSettings.containsKey(Keys.KEY_CONTAINER_MUNICIPALITY)) {
            this.setContainerMunicipality(inputSettings.get(Keys.KEY_CONTAINER_MUNICIPALITY));
        }
        if (inputSettings.containsKey(Keys.KEY_ALIAS)) {
            this.setAlias(inputSettings.get(Keys.KEY_ALIAS));
        }
        if (inputSettings.containsKey(Keys.KEY_IN_MAINTENANCE)) {
            this.setInMaintenance(Boolean.parseBoolean(inputSettings.get(Keys.KEY_IN_MAINTENANCE)));
        }
        if (inputSettings.containsKey(Keys.KEY_GATEWAY_DEVICE)) {
            this.setGatewayDevice(Long.parseLong(inputSettings.get(Keys.KEY_GATEWAY_DEVICE)));
        }
        if (inputSettings.containsKey(Keys.KEY_TECHNICAL_INSTALLATION_DATE)) {
            try {
                this.setTechnicalInstallationDate(format.parse(inputSettings.get(Keys.KEY_TECHNICAL_INSTALLATION_DATE)));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (inputSettings.containsKey(Keys.KEY_DEVICE_MODEL)) {
            this.setDeviceModel(Long.parseLong(inputSettings.get(Keys.KEY_DEVICE_MODEL)));
        }
        if (inputSettings.containsKey(Keys.KEY_IS_ACTIVE)) {
            this.setIsActive(Boolean.parseBoolean(inputSettings.get(Keys.KEY_IS_ACTIVE)));
        }
        return this;
    }
    
    
    @Override
    public Device build() {
        final Device coreDevice = new Device(this.deviceIdentification, this.alias, this.containerCity,
                this.containerPostalCode, this.containerStreet, this.containerNumber,
                this.containerMunicipality, this.gpsLatitude, this.gpsLongitude);

        coreDevice.updateRegistrationData(this.networkAddress, this.deviceType);
        coreDevice.updateProtocol(this.protocolInfo);
        coreDevice.updateInMaintenance(this.inMaintenance);
        coreDevice.updateGatewayDevice(this.gatewayDevice);
        coreDevice.setVersion(this.version);
        coreDevice.setDeviceModel(this.deviceModel);
        coreDevice.setTechnicalInstallationDate(this.technicalInstallationDate);
        return coreDevice;
    }
}
