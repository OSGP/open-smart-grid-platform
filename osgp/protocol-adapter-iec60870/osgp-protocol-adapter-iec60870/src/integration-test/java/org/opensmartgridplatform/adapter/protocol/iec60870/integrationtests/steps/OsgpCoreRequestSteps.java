/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_DEVICE_IDENTIFICATION;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_MESSAGE_TYPE;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.mockito.ArgumentCaptor;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.DeviceRequestMessageListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.OsgpRequestMessageSender;
import org.opensmartgridplatform.dto.da.ConnectRequestDto;
import org.opensmartgridplatform.dto.da.GetHealthStatusRequestDto;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationDto;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ObjectMessageBuilder;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class OsgpCoreRequestSteps {
    @Autowired
    private ConnectionSteps connectionSteps;

    @Autowired
    private DeviceRequestMessageListener messageListener;

    @Autowired
    @Qualifier("protocolIec60870OutboundOsgpCoreRequestsMessageSender")
    private OsgpRequestMessageSender osgpRequestMessageSenderMock;

    @When("I receive a request for the IEC60870 device")
    public void whenIReceiveRequestForIec60870Device() throws JMSException {
        final ObjectMessage message = new ObjectMessageBuilder().withDeviceIdentification(DEFAULT_DEVICE_IDENTIFICATION)
                .withMessageType(DEFAULT_MESSAGE_TYPE)
                .withObject(new GetHealthStatusRequestDto())
                .build();
        this.messageListener.onMessage(message);
    }

    @When("I receive a connect request for IEC60870 device {string} from osgp core")
    public void whenIReceiveAConnectRequestForIEC60870DeviceFromOsgpCore(final String deviceIdentification)
            throws Exception {
        this.connectionSteps.prepareForConnect(deviceIdentification);
        final ObjectMessage message = new ObjectMessageBuilder().withDeviceIdentification(deviceIdentification)
                .withMessageType(MessageType.CONNECT.name())
                .withObject(new ConnectRequestDto())
                .build();
        this.messageListener.onMessage(message);
    }

    @When("I receive a get light sensor status request message for IEC60870 device {string} from osgp core")
    public void whenIReceiveAGetLightSensorStatusRequestMessageForIEC60870DeviceFromOsgpCore(
            final String deviceIdentification) throws Exception {
        this.connectionSteps.prepareForConnect(deviceIdentification);
        final ObjectMessage message = new ObjectMessageBuilder().withDeviceIdentification(deviceIdentification)
                .withMessageType(MessageType.GET_LIGHT_SENSOR_STATUS.name())
                .withObject(new ConnectRequestDto())
                .build();
        this.messageListener.onMessage(message);
    }

    @Then("the protocol adapter should send a light measurement event to osgp core")
    public void theProtocolAdapterShouldSendALightMeasurementEventToOsgpCore(final Map<String, String> eventData) {

        final String expectedEventType = Objects.requireNonNull(eventData.get("event_type"));
        final String expectedDeviceIdentification = Objects.requireNonNull(eventData.get("device_identification"));

        final ArgumentCaptor<RequestMessage> requestMessageCaptor = ArgumentCaptor.forClass(RequestMessage.class);
        verify(this.osgpRequestMessageSenderMock, times(1)).send(requestMessageCaptor.capture(),
                eq(MessageType.EVENT_NOTIFICATION.name()));
        final RequestMessage requestMessage = requestMessageCaptor.getValue();

        assertThat(requestMessage.getDeviceIdentification()).isEqualTo(expectedDeviceIdentification);
        final Serializable request = requestMessage.getRequest();
        assertThat(request).isInstanceOf(EventNotificationDto.class);
        final EventNotificationDto eventNotificationDto = (EventNotificationDto) request;
        assertThat(eventNotificationDto.getEventType().name()).isEqualTo(expectedEventType);
    }
}
