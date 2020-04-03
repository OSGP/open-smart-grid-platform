/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests;

import static org.mockito.Mockito.reset;

import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.LogItemRequestMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import io.cucumber.java.Before;

@ContextConfiguration(classes = { TestConfiguration.class, LightMeasurementTestConfiguration.class,
        DistributionAutomationTestConfiguration.class })
public class TestContextConfiguration {

    @Autowired
    LogItemRequestMessageSender logItemRequestMessageSenderMock;

    @Before
    public void resetContext() {
        reset(this.logItemRequestMessageSenderMock);
    }
}
