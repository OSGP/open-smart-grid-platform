package com.alliander.osgp.platform.dlms.cucumber.builders.entities;

import java.net.InetAddress;
import java.util.Date;
import java.util.Map;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.ProtocolInfo;
import com.alliander.osgp.platform.dlms.cucumber.inputparsers.DateInputParser;
import com.alliander.osgp.platform.dlms.cucumber.steps.Defaults;
import com.alliander.osgp.platform.dlms.cucumber.steps.Keys;

abstract class BaseDeviceBuilder {
    Long version = Defaults.DEFAULT_VERSION;
    String deviceIdentification = Defaults.DEFAULT_DEVICE_IDENTIFICATION;
    String deviceType = Defaults.DEFAULT_DEVICE_TYPE;
    boolean isActivated = Defaults.DEFAULT_IS_ACTIVATED;
    String containerCity = Defaults.DEFAULT_CONTAINER_CITY;
    String containerStreet = Defaults.DEFAULT_CONTAINER_STREET;
    Float gpsLatitude = Defaults.DEFAULT_GPS_LATITUDE;
    Float gpsLongitude = Defaults.DEFAULT_GPS_LONGITUDE;
    String containerPostalCode = Defaults.DEFAULT_CONTAINER_POSTAL_CODE;
    String containerNumber = Defaults.DEFAULT_CONTAINER_NUMBER;
    ProtocolInfo protocolInfo = Defaults.DEFAULT_PROTOCOL_INFO;
    InetAddress networkAddress = Defaults.DEFAULT_NETWORK_ADDRESS;
    String containerMunicipality = Defaults.DEFAULT_CONTAINER_MUNICIPALITY;
    String alias = Defaults.DEFAULT_ALIAS;
    boolean inMaintenance = Defaults.DEFAULT_IN_MAINTENANCE;
    Device gatewayDevice = Defaults.DEFAULT_GATEWAY_DEVICE;
    Date technicalInstallationDate = Defaults.DEFAULT_TECHNICAL_INSTALLATION_DATE;
    DeviceModel deviceModel = Defaults.DEFAULT_DEVICE_MODEL;
    boolean isActive = Defaults.DEFAULT_IS_ACTIVE;

    public BaseDeviceBuilder setVersion(final Long version) {
        this.version = version;
        return this;
    }

    public BaseDeviceBuilder setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
        return this;
    }

    public BaseDeviceBuilder setDeviceType(final String deviceType) {
        this.deviceType = deviceType;
        return this;
    }

    public BaseDeviceBuilder setIsActivated(final boolean isActivated) {
        this.isActivated = isActivated;
        return this;
    }

    public BaseDeviceBuilder setContainerCity(final String containerCity) {
        this.containerCity = containerCity;
        return this;
    }

    public BaseDeviceBuilder setContainerStreet(final String containerStreet) {
        this.containerStreet = containerStreet;
        return this;
    }

    public BaseDeviceBuilder setGpsLatitude(final Float gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
        return this;
    }

    public BaseDeviceBuilder setGpsLongitude(final Float gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
        return this;
    }

    public BaseDeviceBuilder setContainerPostalCode(final String containerPostalCode) {
        this.containerPostalCode = containerPostalCode;
        return this;
    }

    public BaseDeviceBuilder setContainerNumber(final String containerNumber) {
        this.containerNumber = containerNumber;
        return this;
    }

    public BaseDeviceBuilder setProtocolInfo(final ProtocolInfo protocolInfo) {
        this.protocolInfo = protocolInfo;
        return this;
    }

    public BaseDeviceBuilder setNetworkAddress(final InetAddress networkAddress) {
        this.networkAddress = networkAddress;
        return this;
    }

    public BaseDeviceBuilder setContainerMunicipality(final String containerMunicipality) {
        this.containerMunicipality = containerMunicipality;
        return this;
    }

    public BaseDeviceBuilder setAlias(final String alias) {
        this.alias = alias;
        return this;
    }

    public BaseDeviceBuilder setInMaintenance(final Boolean inMaintenance) {
        this.inMaintenance = inMaintenance;
        return this;
    }

    public BaseDeviceBuilder setGatewayDevice(final Device gatewayDevice) {
        this.gatewayDevice = gatewayDevice;
        return this;
    }

    public BaseDeviceBuilder setTechnicalInstallationDate(final Date technicalInstallationDate) {
        this.technicalInstallationDate = technicalInstallationDate;
        return this;
    }

    public BaseDeviceBuilder setDeviceModel(final DeviceModel deviceModel) {
        this.deviceModel = deviceModel;
        return this;
    }

    public BaseDeviceBuilder setIsActive(final boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    public BaseDeviceBuilder withSettings(final Map<String, String> inputSettings) {

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
        if (inputSettings.containsKey(Keys.KEY_CONTAINER_MUNICIPALITY)) {
            this.setContainerMunicipality(inputSettings.get(Keys.KEY_CONTAINER_MUNICIPALITY));
        }
        if (inputSettings.containsKey(Keys.KEY_ALIAS)) {
            this.setAlias(inputSettings.get(Keys.KEY_ALIAS));
        }
        if (inputSettings.containsKey(Keys.KEY_IN_MAINTENANCE)) {
            this.setInMaintenance(Boolean.parseBoolean(inputSettings.get(Keys.KEY_IN_MAINTENANCE)));
        }
        if (inputSettings.containsKey(Keys.KEY_TECHNICAL_INSTALLATION_DATE)) {
            this.setTechnicalInstallationDate(DateInputParser.parse(inputSettings
                    .get(Keys.KEY_TECHNICAL_INSTALLATION_DATE)));
        }
        if (inputSettings.containsKey(Keys.KEY_IS_ACTIVE)) {
            this.setIsActive(Boolean.parseBoolean(inputSettings.get(Keys.KEY_IS_ACTIVE)));
        }
        return this;
    }

}
