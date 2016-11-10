/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.database;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.Then;

public class DeviceLogItemSteps {

    @Autowired
    com.alliander.osgp.platform.cucumber.steps.database.logging.DeviceLogItemSteps deviceLogItemSteps;

    @Then("^the get administrative status communication for device \"([^\"]*)\" should be in the device_log_item table$")
    public void theGetAdministrativeStatusCommunicationForDeviceShouldBeInTheDeviceLogItemTable(
            final String deviceIdentification) throws Throwable {
        this.deviceLogItemSteps
                .theGetAdministrativeStatusCommunicationForDeviceShouldBeInTheDeviceLogItemTable(deviceIdentification);
    }

    @Then("^the get administrative status communication for device \"([^\"]*)\" should not be in the device_log_item table$")
    public void theGetAdministrativeStatusCommunicationForDeviceShouldNotBeInTheDeviceLogItemTable(
            final String deviceIdentification) throws Throwable {
        this.deviceLogItemSteps
                .theGetAdministrativeStatusCommunicationForDeviceShouldNotBeInTheDeviceLogItemTable(deviceIdentification);
    }
}