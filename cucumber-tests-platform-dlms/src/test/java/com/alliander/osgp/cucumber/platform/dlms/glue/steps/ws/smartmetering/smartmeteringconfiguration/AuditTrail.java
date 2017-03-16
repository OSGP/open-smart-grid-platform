/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.alliander.osgp.cucumber.platform.dlms.support.ServiceEndpoint;
import com.alliander.osgp.logging.domain.entities.DeviceLogItem;
import com.alliander.osgp.logging.domain.repositories.DeviceLogItemRepository;

import cucumber.api.java.en.Then;

public class AuditTrail extends SmartMeteringStepsBase {
    private static final String PATH_RESULT = "/Envelope/Body/SetPushSetupAlarmResponse/Result/text()";

    private static final String XPATH_MATCHER_RETRY_OPERATION = "retry count= \\d, correlationuid= \\w*";

    private static final String TEST_SUITE_XML = "SmartmeterConfiguration";
    private static final String TEST_CASE_XML = "125 Receive Alarm Notifications";
    private static final String TEST_CASE_NAME_REQUEST = "SetPushSetupAlarm - Request 1";
    private static final String TEST_CASE_NAME_GETRESPONSE_REQUEST = "GetSetPushSetupAlarmResponse - Request 1";

    private static final Logger LOGGER = LoggerFactory.getLogger(SetPushSetupAlarm.class);

    @Autowired
    private DeviceLogItemRepository deviceLogItemRepository;

    @Autowired
    private ServiceEndpoint serviceEndpoint;

    @Then("^the audit trail contains multiple retry log records$")
    public void theAuditTrailContainsMultipleRetryLogRecords(final Map<String, String> settings) throws Throwable {
        final Pattern responsePattern = Pattern.compile(XPATH_MATCHER_RETRY_OPERATION);

        final String deviceIdentification = getString(settings, Keys.KEY_DEVICE_IDENTIFICATION,
                Defaults.DEFAULT_DEVICE_IDENTIFICATION);

        final List<DeviceLogItem> deviceLogItems = this.findDeviceLogItems(deviceIdentification, 2);
        if (null == deviceLogItems) {
            Assert.fail("DeviceLogItems where not found in the database");
        }

        for (int i = 0; i < 2; i++) {
            final DeviceLogItem item = deviceLogItems.get(i);
            LOGGER.info("CreationTime: {}", item.getCreationTime().toString());
            LOGGER.info("DecodedMessage: {}", item.getDecodedMessage());

            final Matcher responseMatcher = responsePattern.matcher(item.getDecodedMessage());
            assertTrue("A retrying operation is logged.", responseMatcher.find());
        }
    }

    /*
     * it may take some time before the records are to the dev_log_item table,
     * so we have to poll.
     */
    private List<DeviceLogItem> findDeviceLogItems(final String deviceIdentification, final int minExcepted) {
        int loopCount = 0;
        while (loopCount++ < 150) {
            this.sleep(2000L);
            final List<DeviceLogItem> deviceLogItems = this.deviceLogItemRepository
                    .findByDeviceIdentification(deviceIdentification, new PageRequest(0, 2)).getContent();
            if (deviceLogItems != null && deviceLogItems.size() >= minExcepted) {
                return deviceLogItems;
            }
        }
        return null;
    }

    private void sleep(final long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (final InterruptedException e) {
            LOGGER.error("thread sleep was interruped " + e);
        }
    }
}
