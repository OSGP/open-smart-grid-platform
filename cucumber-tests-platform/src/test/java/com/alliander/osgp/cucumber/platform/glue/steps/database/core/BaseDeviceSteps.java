/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.database.core;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getBoolean;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getDate;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getEnum;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getFloat;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getLong;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.GlueBase;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.config.CoreDeviceConfiguration;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.ProtocolInfoRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;

public abstract class BaseDeviceSteps extends GlueBase {

    @Autowired
    private CoreDeviceConfiguration configuration;

    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepository;

    @Autowired
    private DeviceModelRepository deviceModelRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private OrganisationRepository organizationRepository;

    @Autowired
    private ProtocolInfoRepository protocolInfoRepository;

    /**
     * Update an existing device with the given settings.
     *
     * @param device
     *            The Device to update
     * @param settings
     *            The settings to update the device with.
     * @return The Device
     */
    public Device updateDevice(Device device, final Map<String, String> settings) {

        // Now set the optional stuff
        if (settings.containsKey(Keys.KEY_TECHNICAL_INSTALLATION_DATE)
                && !settings.get(Keys.KEY_TECHNICAL_INSTALLATION_DATE).isEmpty()) {
            device.setTechnicalInstallationDate(getDate(settings, Keys.KEY_TECHNICAL_INSTALLATION_DATE).toDate());
        }

        final DeviceModel deviceModel = this.deviceModelRepository
                .findByModelCode(getString(settings, Keys.KEY_DEVICE_MODEL, Defaults.DEFAULT_DEVICE_MODEL_MODEL_CODE));
        device.setDeviceModel(deviceModel);

        device.updateProtocol(this.protocolInfoRepository.findByProtocolAndProtocolVersion(
                getString(settings, Keys.KEY_PROTOCOL, Defaults.DEFAULT_PROTOCOL),
                getString(settings, Keys.KEY_PROTOCOL_VERSION, Defaults.DEFAULT_PROTOCOL_VERSION)));

        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(
                    getString(settings, Keys.KEY_NETWORKADDRESS, this.configuration.getDeviceNetworkAddress()));
        } catch (final UnknownHostException e) {
            inetAddress = InetAddress.getLoopbackAddress();
        }
        device.updateRegistrationData(inetAddress,
                getString(settings, Keys.KEY_DEVICE_TYPE, Defaults.DEFAULT_DEVICE_TYPE));

        device.setVersion(getLong(settings, Keys.KEY_VERSION));
        device.setActive(getBoolean(settings, Keys.KEY_ACTIVE, Defaults.DEFAULT_ACTIVE));
        if (getString(settings, Keys.KEY_ORGANIZATION_IDENTIFICATION, Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION)
                .toLowerCase() != "null") {
            device.addOrganisation(getString(settings, Keys.KEY_ORGANIZATION_IDENTIFICATION,
                    Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
        }
        device.updateMetaData(getString(settings, Keys.KEY_ALIAS, Defaults.DEFAULT_ALIAS),
                getString(settings, Keys.KEY_CITY, Defaults.DEFAULT_CONTAINER_CITY),
                getString(settings, Keys.KEY_POSTCODE, Defaults.DEFAULT_CONTAINER_POSTALCODE),
                getString(settings, Keys.KEY_STREET, Defaults.DEFAULT_CONTAINER_STREET),
                getString(settings, Keys.KEY_NUMBER, Defaults.DEFAULT_CONTAINER_NUMBER),
                getString(settings, Keys.KEY_MUNICIPALITY, Defaults.DEFAULT_CONTAINER_MUNICIPALITY),
                getFloat(settings, Keys.KEY_LATITUDE, Defaults.DEFAULT_LATITUDE),
                getFloat(settings, Keys.KEY_LONGITUDE, Defaults.DEFAULT_LONGITUDE));

        device.setActivated(getBoolean(settings, Keys.KEY_IS_ACTIVATED, Defaults.DEFAULT_IS_ACTIVATED));
        device = this.deviceRepository.save(device);

        if (getString(settings, Keys.KEY_ORGANIZATION_IDENTIFICATION, Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION)
                .toLowerCase() != "null") {
            final Organisation organization = this.organizationRepository.findByOrganisationIdentification(getString(
                    settings, Keys.KEY_ORGANIZATION_IDENTIFICATION, Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
            final DeviceFunctionGroup functionGroup = getEnum(settings, Keys.KEY_DEVICE_FUNCTION_GROUP,
                    DeviceFunctionGroup.class, DeviceFunctionGroup.OWNER);
            final DeviceAuthorization authorization = device.addAuthorization(organization, functionGroup);
            final Device savedDevice = this.deviceRepository.save(device);
            this.deviceAuthorizationRepository.save(authorization);
            ScenarioContext.Current().put(Keys.KEY_DEVICE_IDENTIFICATION, savedDevice.getDeviceIdentification());

            device = savedDevice;
        }

        return device;
    }

    /**
     * Update a device entity given its device identification.
     *
     * @param deviceIdentification
     *            The deviceIdentification of the device to update.
     * @param settings
     *            The settings to update the device with.
     *
     * @return The Device.
     */
    public Device updateDevice(final String deviceIdentification, final Map<String, String> settings) {
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
        return this.updateDevice(device, settings);
    }
}
