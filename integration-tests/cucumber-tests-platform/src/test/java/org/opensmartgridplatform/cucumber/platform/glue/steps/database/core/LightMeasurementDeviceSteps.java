/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getFloat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getShort;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.repositories.LightMeasurementDeviceRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class LightMeasurementDeviceSteps extends BaseDeviceSteps {

    @Autowired
    private LightMeasurementDeviceRepository lightMeasurementDeviceRepository;

    @Then("^the light measurement device exists")
    public void theLigthMeasurementDeviceExists(final Map<String, String> settings) throws Throwable {
        final LightMeasurementDevice lmd = Wait.untilAndReturn(() -> {
            final LightMeasurementDevice entity = this.lightMeasurementDeviceRepository
                    .findByDeviceIdentification(settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
            if (entity == null) {
                throw new Exception(
                        "Device with identification [" + settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION) + "]");
            }

            return entity;
        });

        if (settings.containsKey(PlatformKeys.ALIAS)) {
            assertThat(lmd.getAlias()).isEqualTo(getString(settings, PlatformKeys.ALIAS));
        }
        if (settings.containsKey(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION)) {
            assertThat(lmd.getOwner().getOrganisationIdentification())
                    .isEqualTo(getString(settings, PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION));
        }
        if (settings.containsKey(PlatformKeys.CONTAINER_POSTALCODE)) {
            assertThat(lmd.getContainerAddress().getPostalCode())
                    .isEqualTo(getString(settings, PlatformKeys.CONTAINER_POSTALCODE));
        }
        if (settings.containsKey(PlatformKeys.CONTAINER_CITY)) {
            assertThat(lmd.getContainerAddress().getCity()).isEqualTo(getString(settings, PlatformKeys.CONTAINER_CITY));
        }
        if (settings.containsKey(PlatformKeys.CONTAINER_STREET)) {
            assertThat(lmd.getContainerAddress().getStreet())
                    .isEqualTo(getString(settings, PlatformKeys.CONTAINER_STREET));
        }
        if (settings.containsKey(PlatformKeys.CONTAINER_NUMBER)) {
            assertThat(lmd.getContainerAddress().getNumber())
                    .isEqualTo(getString(settings, PlatformKeys.CONTAINER_NUMBER));
        }
        if (settings.containsKey(PlatformKeys.CONTAINER_MUNICIPALITY)) {
            assertThat(lmd.getContainerAddress().getMunicipality())
                    .isEqualTo(getString(settings, PlatformKeys.CONTAINER_MUNICIPALITY));
        }
        if (settings.containsKey(PlatformKeys.KEY_LATITUDE)) {
            assertThat(lmd.getGpsCoordinates().getLatitude()).isEqualTo(getFloat(settings, PlatformKeys.KEY_LATITUDE));
        }
        if (settings.containsKey(PlatformKeys.KEY_LONGITUDE)) {
            assertThat(lmd.getGpsCoordinates().getLongitude())
                    .isEqualTo(getFloat(settings, PlatformKeys.KEY_LONGITUDE));
        }
        if (settings.containsKey(PlatformKeys.KEY_ACTIVATED)) {
            assertThat(lmd.isActivated()).isEqualTo(getBoolean(settings, PlatformKeys.KEY_ACTIVATED));
        }
        if (settings.containsKey(PlatformKeys.KEY_DEVICE_LIFECYCLE_STATUS)) {
            assertThat(lmd.getDeviceLifecycleStatus()).isEqualTo(
                    getEnum(settings, PlatformKeys.KEY_DEVICE_LIFECYCLE_STATUS, DeviceLifecycleStatus.class));
        }
        if (settings.containsKey(PlatformKeys.KEY_DEVICE_MODEL_MODELCODE)) {
            assertThat(lmd.getDeviceModel().getModelCode())
                    .isEqualTo(getString(settings, PlatformKeys.KEY_DEVICE_MODEL_MODELCODE));
        }
        if (settings.containsKey(PlatformKeys.KEY_LMD_DESCRIPTION)) {
            assertThat(lmd.getDescription()).isEqualTo(getString(settings, PlatformKeys.KEY_LMD_DESCRIPTION));
        }
        if (settings.containsKey(PlatformKeys.KEY_LMD_CODE)) {
            assertThat(lmd.getCode()).isEqualTo(getString(settings, PlatformKeys.KEY_LMD_CODE));
        }
        if (settings.containsKey(PlatformKeys.KEY_LMD_COLOR)) {
            assertThat(lmd.getColor()).isEqualTo(getString(settings, PlatformKeys.KEY_LMD_COLOR));
        }
        if (settings.containsKey(PlatformKeys.KEY_LMD_DIGITAL_INPUT)) {
            assertThat(lmd.getDigitalInput()).isEqualTo(getShort(settings, PlatformKeys.KEY_LMD_DIGITAL_INPUT));
        }
    }

    @Given("^the light measurement devices$")
    @Transactional("txMgrCore")
    public void theLightMeasurementDevices() throws Throwable {
        this.createLightMeasurementDevices();
    }

    /**
     * Create the 4 light measurement devices and {@link DeviceAuthorization}s
     * for the default organization.
     */
    public void createLightMeasurementDevices() {
        final LightMeasurementDevice lmd01 = this.createLightMeasurementDevice("LMD-01", "N-01", "#c9eec9", (short) 1);

        // Set the last communication time to 2017-08-01 at 13:00 UTC
        final Date lastCommunicationTimeLmd01 = new GregorianCalendar(2017, Calendar.AUGUST, 1, 13, 0).getTime();
        lmd01.setLastCommunicationTime(lastCommunicationTimeLmd01);
        this.lightMeasurementDeviceRepository.save(lmd01);

        this.createLightMeasurementDevice("LMD-02", "E-01", "#eec9c9", (short) 2);
        this.createLightMeasurementDevice("LMD-03", "S-01", "#c9c9ee", (short) 3);
        this.createLightMeasurementDevice("LMD-04", "W-01", "#eeeec9", (short) 4);
    }

    /**
     * Create a single light measurement device, including rights for the
     * default organization.
     */
    @Transactional("txMgrCore")
    public LightMeasurementDevice createLightMeasurementDevice(final String deviceIdentification, final String code,
            final String color, final short digitalInput) {
        final String deviceType = "LMD";
        final InetAddress networkAddress = InetAddress.getLoopbackAddress();
        final Date technicalInstallationDate = DateTime.now().withZone(DateTimeZone.UTC).toDate();
        final ProtocolInfo protocolInfo = this.protocolInfoRepository.findByProtocolAndProtocolVersion("IEC61850",
                "1.0");

        final LightMeasurementDevice lightMeasurementDevice = new LightMeasurementDevice(deviceIdentification);
        lightMeasurementDevice.setTechnicalInstallationDate(technicalInstallationDate);
        lightMeasurementDevice.updateRegistrationData(networkAddress, deviceType);
        lightMeasurementDevice.updateProtocol(protocolInfo);
        lightMeasurementDevice.updateInMaintenance(false);
        lightMeasurementDevice.setDescription(deviceIdentification);
        lightMeasurementDevice.setCode(code);
        lightMeasurementDevice.setColor(color);
        lightMeasurementDevice.setLastCommunicationTime(technicalInstallationDate);
        lightMeasurementDevice.setDigitalInput(digitalInput);

        // Setting the default authorization both creates the device and adds
        // the device authorization.
        this.setDefaultDeviceAuthorizationForDevice(lightMeasurementDevice);

        return this.lightMeasurementDeviceRepository.findByDeviceIdentification(deviceIdentification);
    }
}
