/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.core.audittrail;

import static com.alliander.osgp.cucumber.core.Helpers.getString;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ResponseNotifier;
import com.alliander.osgp.logging.domain.entities.DeviceLogItem;
import com.alliander.osgp.logging.domain.repositories.DeviceLogItemRepository;

import cucumber.api.java.en.Then;

public class AuditTrail {
    private static final String PATTERN_RETRY_OPERATION = "retry count= .*, correlationuid= .*";

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditTrail.class);

    @Autowired
    private DeviceLogItemRepository deviceLogItemRepository;

    @Autowired
    protected ResponseNotifier responseNotifier;

    @Then("^the audit trail contains multiple retry log records$")
    public void theAuditTrailContainsMultipleRetryLogRecords(final Map<String, String> settings) throws Throwable {
        final String deviceIdentification = getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION);

        assertTrue("DeviceLogItems are not found in the database",
                // Wait 10 minutes with a timeout of 1 sec for the logged retry exceptions
                this.responseNotifier.waitForLog(deviceIdentification, 1000, 600000));

        final List<DeviceLogItem> deviceLogItems = this.deviceLogItemRepository
                .findByDeviceIdentification(deviceIdentification, new PageRequest(0, 2)).getContent();

        for (final DeviceLogItem deviceLogItem : deviceLogItems) {
            LOGGER.info("CreationTime: {}", deviceLogItem.getCreationTime().toString());
            LOGGER.info("DecodedMessage: {}", deviceLogItem.getDecodedMessage());

            final boolean isMatchRetryOperation = Pattern.matches(PATTERN_RETRY_OPERATION,
                    deviceLogItem.getDecodedMessage());
            assertTrue("No retry log item found.", isMatchRetryOperation);
        }
    }
}
