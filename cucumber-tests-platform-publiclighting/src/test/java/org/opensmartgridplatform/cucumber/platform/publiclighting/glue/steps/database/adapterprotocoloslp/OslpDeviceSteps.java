/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps.database.adapterprotocoloslp;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.entities.OslpDevice;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.repositories.OslpDeviceRepository;
import org.opensmartgridplatform.cucumber.core.GlueBase;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.glue.steps.database.core.SsldDeviceSteps;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * OSLP device specific steps.
 */
public class OslpDeviceSteps extends GlueBase {

    public static final String DEFAULT_DEVICE_UID = "dGVzdDEyMzQ1Njc4";
    private static final String DEVICE_PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEFhUImXFJdqmputquVAc2CPdnn9Ju"
            + "00M3m/Ice7wABNN+oAYKQbw/OceqvZmFF1+r4nO/vCm/f1JO5nEorE2jNQ==";

    @Autowired
    private OslpDeviceRepository oslpDeviceRepository;

    @Autowired
    private SsldDeviceSteps ssldDeviceSteps;

    @Given("^an ssld oslp device$")
    public void anSsldOslpDevice(final Map<String, String> settings) {

        // First create the device itself in the OSGP core database
        this.ssldDeviceSteps.anSsldDevice(settings);

        // Now create the OSLP device in the OSLP database
        final String deviceIdentification = getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION);
        final OslpDevice device = new OslpDevice(getString(settings, PlatformKeys.KEY_DEVICE_UID, DEFAULT_DEVICE_UID),
                deviceIdentification,
                getString(settings, PlatformKeys.KEY_DEVICE_TYPE, PlatformDefaults.DEFAULT_DEVICE_TYPE));
        device.setSequenceNumber(0);
        device.setRandomDevice(0);
        device.setRandomPlatform(0);
        device.updatePublicKey(DEVICE_PUBLIC_KEY);
        this.oslpDeviceRepository.save(device);
    }

    @Given("^(\\d++) ssld oslp devices$")
    public void ssldOslpDevices(final int numberOfDevices, final Map<String, String> settings) {

        for (int i = 0; i < numberOfDevices; i++) {
            final String deviceIdentification = "TST-" + (i + 1);
            final Map<String, String> deviceSettings = new HashMap<>();
            deviceSettings.put(PlatformKeys.KEY_DEVICE_IDENTIFICATION, deviceIdentification);
            deviceSettings.put(PlatformKeys.KEY_DEVICE_UID,
                    Base64.encodeBase64String(deviceIdentification.getBytes(StandardCharsets.US_ASCII)));
            deviceSettings.put(PlatformKeys.KEY_VERSION, "0");
            deviceSettings.putAll(settings);
            this.anSsldOslpDevice(deviceSettings);
        }
    }

    @Then("^the ssld oslp device contains$")
    public void theSsldOslpDeviceContains(final Map<String, String> expectedEntity) {

        Wait.until(() -> {
            final OslpDevice entity = this.oslpDeviceRepository
                    .findByDeviceIdentification(getString(expectedEntity, PlatformKeys.KEY_DEVICE_IDENTIFICATION));

            Assert.assertEquals(getString(expectedEntity, PlatformKeys.KEY_DEVICE_TYPE), entity.getDeviceType());
            Assert.assertEquals(getString(expectedEntity, PlatformKeys.KEY_DEVICE_UID), entity.getDeviceUid());
        });

        this.ssldDeviceSteps.theSsldDeviceContains(expectedEntity);
    }
}
