/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.glue.steps.database.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.opensmartgridplatform.cucumber.core.GlueBase;
import org.opensmartgridplatform.logging.domain.repositories.DeviceLogItemRepository;

import cucumber.api.java.en.Then;

public class DeviceLogItemSteps extends GlueBase {

    @Autowired
    private DeviceLogItemRepository deviceLogItemRepository;

    @Then("^the get administrative status communication for device \"([^\"]*)\" should be in the device_log_item table$")
    public void theGetAdministrativeStatusCommunicationForDeviceShouldBeInTheDeviceLogItemTable(
            final String deviceIdentification) throws Throwable {

        final Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
        final Page<org.opensmartgridplatform.logging.domain.entities.DeviceLogItem> deviceLogPage = this.deviceLogItemRepository
                .findByDeviceIdentification(deviceIdentification, pageable);

        assertTrue("device log items are present", deviceLogPage.hasContent());

        final List<org.opensmartgridplatform.logging.domain.entities.DeviceLogItem> deviceLogItems = deviceLogPage
                .getContent();
        for (final org.opensmartgridplatform.logging.domain.entities.DeviceLogItem deviceLogItem : deviceLogItems) {
            assertNotNull("encoded message", deviceLogItem.getEncodedMessage());
            assertTrue("\"GET_ADMINISTRATIVE_STATUS\" is part of the decoded message",
                    deviceLogItem.getDecodedMessage().contains("GET_ADMINISTRATIVE_STATUS"));
        }
    }

    @Then("^the get administrative status communication for device \"([^\"]*)\" should not be in the device_log_item table$")
    public void theGetAdministrativeStatusCommunicationForDeviceShouldNotBeInTheDeviceLogItemTable(
            final String deviceIdentification) throws Throwable {

        final Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
        final Page<org.opensmartgridplatform.logging.domain.entities.DeviceLogItem> deviceLogPage = this.deviceLogItemRepository
                .findByDeviceIdentification(deviceIdentification, pageable);

        assertEquals("number of device log items for " + deviceIdentification, 0, deviceLogPage.getTotalElements());
    }
}
