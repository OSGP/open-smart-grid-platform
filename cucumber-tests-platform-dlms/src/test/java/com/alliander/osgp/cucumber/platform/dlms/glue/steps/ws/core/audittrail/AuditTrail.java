/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.core.audittrail;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.alliander.osgp.cucumber.platform.dlms.support.ResponseNotifier;
import com.alliander.osgp.logging.domain.entities.DeviceLogItem;
import com.alliander.osgp.logging.domain.repositories.DeviceLogItemRepository;

import cucumber.api.java.en.Then;

public class AuditTrail extends SmartMeteringStepsBase {
    private static final String XPATH_MATCHER_RETRY_OPERATION = "retry count= \\d, correlationuid= \\w*";

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditTrail.class);

    private static final Integer MULTIPLE_RETRY_COUNT = 2;

    @Autowired
    private DeviceLogItemRepository deviceLogItemRepository;

    @Autowired
    protected ResponseNotifier responseNotifier;

    @Then("^the audit trail contains multiple retry log records$")
    public void theAuditTrailContainsMultipleRetryLogRecords(final Map<String, String> settings) throws Throwable {
        final Pattern responsePattern = Pattern.compile(XPATH_MATCHER_RETRY_OPERATION);

        final String deviceIdentification = getString(settings, Keys.KEY_DEVICE_IDENTIFICATION,
                Defaults.DEFAULT_DEVICE_IDENTIFICATION);

        assertTrue("DeviceLogItems are not found in the database",
                this.responseNotifier.waitForLog(deviceIdentification, 0, 3000000));

        final List<DeviceLogItem> deviceLogItems = this.deviceLogItemRepository
                .findByDeviceIdentification(deviceIdentification, new PageRequest(0, 2)).getContent();

        for (int i = 0; i < MULTIPLE_RETRY_COUNT; i++) {
            final DeviceLogItem item = deviceLogItems.get(i);
            LOGGER.info("CreationTime: {}", item.getCreationTime().toString());
            LOGGER.info("DecodedMessage: {}", item.getDecodedMessage());

            final Matcher responseMatcher = responsePattern.matcher(item.getDecodedMessage());
            assertTrue("No retry log item found.", responseMatcher.find());
        }
    }

}
