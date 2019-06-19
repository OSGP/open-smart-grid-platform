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
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.List;
import java.util.Map;

import org.opensmartgridplatform.cucumber.core.GlueBase;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.opensmartgridplatform.logging.domain.repositories.DeviceLogItemPagingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class DeviceLogItemSteps extends GlueBase {

    @Autowired
    private DeviceLogItemPagingRepository deviceLogItemRepository;

    @Given("^I have a device log item$")
    public void iHaveADeviceLogItem(final Map<String, String> settings) {

        final String deviceIdentification = getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION);
        final String deviceUid = getString(settings, PlatformKeys.KEY_DEVICE_UID, PlatformDefaults.DEVICE_UID);
        final String decodedMessage = "O S L P";
        final String encodedMessage = "0x4F 0x53 0x4C 0x50";
        final boolean incoming = true;
        final String organisationIdentification = getString(settings, PlatformKeys.KEY_ORGANIZATION,
                PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION);
        final Integer payloadMessageSerializedSize = 4;
        final boolean valid = true;

        final DeviceLogItem deviceLogItem = new DeviceLogItem.Builder().withDeviceIdentification(deviceIdentification)
                .withDeviceUid(deviceUid).withDecodedMessage(decodedMessage).withEncodedMessage(encodedMessage)
                .withIncoming(incoming).withOrganisationIdentification(organisationIdentification)
                .withPayloadMessageSerializedSize(payloadMessageSerializedSize).withValid(valid).build();

        this.deviceLogItemRepository.save(deviceLogItem);
    }

    @Given("^I have (\\d+) device log items$")
    public void iHaveDeviceLogItems(final int number, final Map<String, String> settings) {
        for (int i = 0; i < number; i++) {
            this.iHaveADeviceLogItem(settings);
        }
    }

    @Then("^the get administrative status communication for device \"([^\"]*)\" should be in the device_log_item "
            + "table$")
    public void theGetAdministrativeStatusCommunicationForDeviceShouldBeInTheDeviceLogItemTable(
            final String deviceIdentification) {

        final Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
        final List<org.opensmartgridplatform.logging.domain.entities.DeviceLogItem> deviceLogItems = this.deviceLogItemRepository
                .findByDeviceIdentification(deviceIdentification, pageable).getContent();
        assertTrue("number of device log items for " + deviceIdentification,
                this.countGetAdministrativeStatusLogItems(deviceLogItems) > 0);
    }

    @Then("^the get administrative status communication for device \"([^\"]*)\" should not be in the device_log_item "
            + "table$")
    public void theGetAdministrativeStatusCommunicationForDeviceShouldNotBeInTheDeviceLogItemTable(
            final String deviceIdentification) {

        final Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
        final List<org.opensmartgridplatform.logging.domain.entities.DeviceLogItem> deviceLogItems = this.deviceLogItemRepository
                .findByDeviceIdentification(deviceIdentification, pageable).getContent();
        assertEquals("number of device log items for " + deviceIdentification, 0,
                this.countGetAdministrativeStatusLogItems(deviceLogItems));
    }

    private long countGetAdministrativeStatusLogItems(final List<DeviceLogItem> deviceLogItems) {
        return deviceLogItems.stream().filter(deviceLogItem -> this.isGetAdministrativeStatusLogItem(deviceLogItem))
                .count();
    }

    private boolean isGetAdministrativeStatusLogItem(final DeviceLogItem deviceLogItem) {
        return deviceLogItem.getEncodedMessage() != null
                && deviceLogItem.getDecodedMessage().contains("GET_ADMINISTRATIVE_STATUS");
    }
}
