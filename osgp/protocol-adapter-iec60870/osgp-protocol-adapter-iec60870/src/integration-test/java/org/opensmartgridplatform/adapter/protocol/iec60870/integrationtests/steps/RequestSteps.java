/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.steps;

import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_DEVICE_IDENTIFICATION;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_MESSAGE_TYPE;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.DeviceRequestMessageListener;
import org.opensmartgridplatform.dto.da.GetHealthStatusRequestDto;
import org.opensmartgridplatform.shared.infra.jms.ObjectMessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.When;

public class RequestSteps {
    @Autowired
    private DeviceRequestMessageListener messageListener;

    @When("I receive a request for the IEC60870 device")
    public void whenIReceiveRequestForIec60870Device() throws JMSException {
        final ObjectMessage message = new ObjectMessageBuilder().withDeviceIdentification(DEFAULT_DEVICE_IDENTIFICATION)
                .withMessageType(DEFAULT_MESSAGE_TYPE)
                .withObject(new GetHealthStatusRequestDto())
                .build();
        this.messageListener.onMessage(message);
    }
}
