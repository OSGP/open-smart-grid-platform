/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.core;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the create organization requests steps
 */
public class CreateDeviceSteps extends CoreStepsBase {

    protected CreateDeviceSteps() throws Throwable {
        super();
        // TODO Auto-generated constructor stub
    }

    private static final String TEST_SUITE = "DeviceInstallation";
    private static final String TEST_CASE_NAME_ADD_DEVICE = "AT AddDevice";
    private static final String TEST_CASE_NAME_REQUEST_ADD_DEVICE = "AddDevice";
    private static final String TEST_CASE_NAME_UPDATE_DEVICE = "AT UpdateDevice";
    private static final String TEST_CASE_NAME_REQUEST_UPDATE_DEVICE = "UpdateDevice";

    @Autowired
    private DeviceRepository deviceRepository;

    /**
     *
     * @param requestParameters
     */
    private void fillPropertiesMap(final Map<String, String> requestParameters) {
        PROPERTIES_MAP.put("__DEVICE_IDENTIFICATION__", requestParameters.get("DeviceIdentification"));
        PROPERTIES_MAP.put("__DEVICEMODEL_MANUFACTURER__", requestParameters.get("DeviceModelManufacturer"));
        if (requestParameters.containsKey("DeviceAlias")) {
            PROPERTIES_MAP.put("__ALIAS__", requestParameters.get("DeviceAlias"));
        }
        if (requestParameters.containsKey("GpsLatitude")) {
            PROPERTIES_MAP.put("__GPS_LATITUDE__", requestParameters.get("GpsLatitude"));
        }
        if (requestParameters.containsKey("GpsLongitude")) {
            PROPERTIES_MAP.put("__GPS_LONGITUDE__", requestParameters.get("GpsLongitude"));
        }
        if (requestParameters.containsKey("Activated")) {
            PROPERTIES_MAP.put("__ACTIVATED__", requestParameters.get("Activated").toLowerCase());
        }
        if (requestParameters.containsKey("HasSchedule")) {
            PROPERTIES_MAP.put("__HAS_SCHEDULE__", requestParameters.get("HasSchedule").toLowerCase());
        }
        if (requestParameters.containsKey("PublicKeyPresent")) {
            PROPERTIES_MAP.put("__PUBLIC_KEY_PRESENT__", requestParameters.get("PublicKeyPresent").toLowerCase());
        }
        if (requestParameters.containsKey("DeviceModelManufacturer")) {
            PROPERTIES_MAP.put("__DEVICE_MODEL_MANUFACTURER__", requestParameters.get("DeviceModelManufacturer"));
        }
    }

    /**
     * Generic method to check if the device is created as expected in the
     * database.
     *
     * @param expectedEntity
     *            The expected settings.
     * @throws Throwable
     */
    @Then("^the entity device exists$")
    public void thenTheEntityDeviceExists(final Map<String, String> expectedEntity) throws Throwable {
        boolean success = false;
        int count = 0;
        while (!success) {
            try {
                if (count > 120) {
                    Assert.fail("Failed");
                }

                // Wait for next try to retrieve a response
                count++;
                Thread.sleep(1000);

                final Device device = this.deviceRepository
                        .findByDeviceIdentification(expectedEntity.get("DeviceIdentification"));

                Assert.assertEquals(expectedEntity.get("DeviceModelManufacturer"),
                        device.getDeviceModel().getManufacturerId());
                Assert.assertEquals(expectedEntity.get("Alias"), device.getAlias());

                success = true;
            } catch (final Exception e) {
                // Do nothing
            }
        }
    }

    /**
     *
     * @throws Throwable
     */
    @When("^receiving an add device request$")
    public void receiving_an_add_device_request(final Map<String, String> requestParameters) throws Throwable {

        this.fillPropertiesMap(requestParameters);

        this.requestRunner(TestStepStatus.UNKNOWN, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST_ADD_DEVICE,
                TEST_CASE_NAME_ADD_DEVICE, TEST_SUITE);
    }

    /**
     *
     * @throws Throwable
     */
    @When("^updating a device$")
    public void updating_a_device(final Map<String, String> requestParameters) throws Throwable {

        this.fillPropertiesMap(requestParameters);

        this.requestRunner(TestStepStatus.UNKNOWN, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST_UPDATE_DEVICE,
                TEST_CASE_NAME_UPDATE_DEVICE, TEST_SUITE);
    }
}