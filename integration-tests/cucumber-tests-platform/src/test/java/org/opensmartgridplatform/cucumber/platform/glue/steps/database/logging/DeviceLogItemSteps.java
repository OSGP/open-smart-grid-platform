/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.glue.steps.database.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import cucumber.api.java.en.Then;
import org.opensmartgridplatform.cucumber.core.GlueBase;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.opensmartgridplatform.logging.domain.repositories.DeviceLogItemPagingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class DeviceLogItemSteps extends GlueBase {

    @Autowired
    private DeviceLogItemPagingRepository deviceLogItemRepository;

    @Then("^the get administrative status communication for device \"([^\"]*)\" should be in the device_log_item "
            + "table$")
    public void theGetAdministrativeStatusCommunicationForDeviceShouldBeInTheDeviceLogItemTable(
            final String deviceIdentification) {

        final Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
        final List<org.opensmartgridplatform.logging.domain.entities.DeviceLogItem> deviceLogItems =
                this.deviceLogItemRepository
                .findByDeviceIdentification(deviceIdentification, pageable).getContent();
        assertTrue("number of device log items for " + deviceIdentification,
                this.countGetAdministrativeStatusLogItems(deviceLogItems) > 0);
    }

    @Then("^the get administrative status communication for device \"([^\"]*)\" should not be in the device_log_item "
            + "table$")
    public void theGetAdministrativeStatusCommunicationForDeviceShouldNotBeInTheDeviceLogItemTable(
            final String deviceIdentification) {

        final Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
        final List<org.opensmartgridplatform.logging.domain.entities.DeviceLogItem> deviceLogItems =
                this.deviceLogItemRepository
                .findByDeviceIdentification(deviceIdentification, pageable).getContent();
        assertEquals("number of device log items for " + deviceIdentification, 0,
                this.countGetAdministrativeStatusLogItems(deviceLogItems));
    }

    private long countGetAdministrativeStatusLogItems(final List<DeviceLogItem> deviceLogItems) {
        return deviceLogItems.stream().filter(deviceLogItem -> this.isGetAdministrativeStatusLogItem(deviceLogItem))
                .count();
    }

    private boolean isGetAdministrativeStatusLogItem(final DeviceLogItem deviceLogItem) {
        return deviceLogItem.getEncodedMessage() != null && deviceLogItem.getDecodedMessage()
                .contains("GET_ADMINISTRATIVE_STATUS");
    }
}
