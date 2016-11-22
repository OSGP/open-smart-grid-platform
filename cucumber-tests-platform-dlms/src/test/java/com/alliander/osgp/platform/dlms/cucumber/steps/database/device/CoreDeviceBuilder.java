/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.database.device;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

public class CoreDeviceBuilder implements Builder<Device>{

    private Long version = Defaults.DEFAULT_VERSION;
    private String deviceIdentification;
    private String deviceType;
    private boolean isActivated;   
    private String containerCity;
    private String containerStreet;
    private Float gpsLatitude;
    private Float gpsLongitude;
    private String containerPostalCode;
    private String containerNumber;
    private Long protocolInfo;
    private Long networkAddress;
    private String containerMunicipality;
    private String alias;
    private boolean inMaintenance;
    private Long gatewayDevice;
    private Date technicalInstallationDate;
    private Long deviceModel;
    private boolean isActive;
    
             
    private String organisationId = "test-org";
    
    private final String deviceId = null;

    public CoreDeviceBuilder setVersion(final Long version) {
        this.version = version;
        return this;
    }
    
    public CoreDeviceBuilder setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
        return this;
    }
    
    public CoreDeviceBuilder setDeviceType(final String deviceType) {
        this.deviceType = deviceType;
        return this;
    }
    
    public CoreDeviceBuilder setIsActivated(final boolean isActivated) {
        this.isActivated = isActivated;
        return this;
    }
    
    public CoreDeviceBuilder setContainerCity(final String containerCity) {
        this.containerCity = containerCity;
        return this;
    }
    
    public CoreDeviceBuilder setContainerStreet(final String containerStreet) {
        this.containerStreet = containerStreet;
        return this;
    }
    
    public CoreDeviceBuilder setGpsLatitude(final Float gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
        return this;
    }
    
    public CoreDeviceBuilder setGpsLongitude(final Float gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
        return this;
    }
    
    public CoreDeviceBuilder setContainerPostalCode(String containerPostalCode) {
        this.containerPostalCode = containerPostalCode;
        return this;
    }
    
    public CoreDeviceBuilder setContainerNumber(String containerNumber) {
        this.containerNumber = containerNumber;
        return this;
    }
    
    public CoreDeviceBuilder setProtocolInfo(Long protocolInfo) {
        this.protocolInfo = protocolInfo;
        return this;
    }
    
    public CoreDeviceBuilder setNetworkAddress(Long networkAddress) {
        this.networkAddress = networkAddress;
        return this;
    }
    
    public CoreDeviceBuilder setContainerMunicipality(String containerMunicipality) {
        this.containerMunicipality = containerMunicipality;
        return this;
    }
    
    public CoreDeviceBuilder setAlias(String alias) {
        this.alias = alias;
        return this;
    }
    
    public CoreDeviceBuilder setInMaintenance(Boolean inMaintenance) {
        this.inMaintenance = inMaintenance;
        return this;
    }
    
    public CoreDeviceBuilder setGatewayDevice(Long gatewayDevice) {
        this.gatewayDevice = gatewayDevice;
        return this;
    }
    
    public CoreDeviceBuilder setTechnicalInstallationDate(Date technicalInstallationDate) {
        this.technicalInstallationDate = technicalInstallationDate;
        return this;
    }
    
    public CoreDeviceBuilder setDeviceModel(Long deviceModel) {
        this.deviceModel = deviceModel;
        return this;
    }
    
    public CoreDeviceBuilder setIsActive(boolean isActive) {
        this.isActive = isActive;
        return this;
    }
    
    
    
    
    
    
    
    public CoreDeviceBuilder setOrganisationId(final String organisationId) {
        this.organisationId = organisationId;
        return this;
    }
    
    
    public CoreDeviceBuilder buildDevice(final Map<String, String> inputSettings) {
        
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
            this.setNetworkAddress(Long.parseLong(inputSettings.get(Keys.KEY_NETWORK_ADDRESS)));
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
            this.setTechnicalInstallationDate((inputSettings.get(Keys.KEY_TECHNICAL_INSTALLATION_DATE)));
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
        final Device coreDevice = new Device();
        return coreDevice;
    }


}
