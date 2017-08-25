/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.database.core;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.LightMeasurementDevice;
import com.alliander.osgp.domain.core.entities.ProtocolInfo;
import com.alliander.osgp.domain.core.repositories.LightMeasurementDeviceRepository;

import cucumber.api.java.en.Given;

public class LightMeasurementDeviceSteps extends BaseDeviceSteps {

    @Autowired
    private LightMeasurementDeviceRepository lightMeasurementDeviceRepository;

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
        List<LightMeasurementDevice> devices = new ArrayList<>();
        final LightMeasurementDevice lmd01 = this.createLightMeasurementDevice("LMD-01", "N-01", "#c9eec9", (short) 1);
        // Set the last communication time to 2017-08-01 at 13:00 UTC
        final Date lastCommunicationTimeLmd01 = new GregorianCalendar(2017, Calendar.AUGUST, 1, 13, 0).getTime();

        lmd01.setLastCommunicationTime(lastCommunicationTimeLmd01);
        devices.add(lmd01);
        devices.add(this.createLightMeasurementDevice("LMD-02", "E-01", "#eec9c9", (short) 2));
        devices.add(this.createLightMeasurementDevice("LMD-03", "S-01", "#c9c9ee", (short) 3));
        devices.add(this.createLightMeasurementDevice("LMD-04", "W-01", "#eeeec9", (short) 4));

        devices = this.lightMeasurementDeviceRepository.save(devices);

        for (final LightMeasurementDevice device : devices) {
            this.setDefaultDeviceAuthorizationForDevice(device);
        }
    }

    /**
     * Create a single light measurement device.
     */
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

        return lightMeasurementDevice;
    }

}
