/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.steps;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.matchers.MeasurementReportTypeMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.Then;

public class ResponseSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseSteps.class);

    @Autowired
    private DeviceResponseMessageSender responseMessageSenderMock;

    @Then("I should send a measurement report of type {string} to the platform")
    public void thenIShouldSendMeasurementReportOfType(final String typeId) {
        LOGGER.debug("Then I should send a measurement report of type {}", typeId);

        verify(this.responseMessageSenderMock).send(argThat(new MeasurementReportTypeMatcher(typeId)));
    }
}
