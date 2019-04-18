/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.steps;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ConnectionEventListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.Iec60870DeviceFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionCache;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceConnectionParameters;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.DeviceRequestMessageListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking.Iec60870Client;
import org.opensmartgridplatform.dto.da.GetHealthStatusRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.ConnectionFailureException;
import org.opensmartgridplatform.shared.infra.jms.ObjectMessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ConnectToDeviceSteps {

    private static final String DEVICE_IDENTIFICATION = "TestDevice";
    private static final String MESSAGE_TYPE = "GET_HEALTH_STATUS";

    @Autowired
    private DeviceRequestMessageListener messageListener;

    @Autowired
    private Iec60870DeviceRepository repository;

    @Autowired
    private ClientConnectionCache cache;

    @Autowired
    private Iec60870Client client;

    @Given("an IEC60870 device")
    public void givenAnIec60870Device() {
        final Iec60870Device device = Iec60870DeviceFactory.createDefaultWith(DEVICE_IDENTIFICATION);
        when(this.repository.findByDeviceIdentification(any(String.class))).thenReturn(device);
    }

    @Given("the IEC60870 device is not connected")
    public void givenTheIec60870DeviceIsNotConnected() throws ConnectionFailureException {
        // Make sure the connection is not present in the cache on the first
        // call
        when(this.cache.getConnection(DEVICE_IDENTIFICATION)).thenReturn(null).thenCallRealMethod();

        // Make sure the client connect works as expected
        final DeviceConnection deviceConnection = new DeviceConnection(mock(Connection.class),
                new DeviceConnectionParameters.Builder().deviceIdentification(DEVICE_IDENTIFICATION).build());
        when(this.client.connect(any(DeviceConnectionParameters.class), any(ConnectionEventListener.class)))
                .thenReturn(deviceConnection);
    }

    @When("I receive a request for the IEC60870 device")
    public void whenIReceiveARequestForTheIec60870Device() throws JMSException {
        final ObjectMessage message = new ObjectMessageBuilder().withDeviceIdentification(DEVICE_IDENTIFICATION)
                .withMessageType(MESSAGE_TYPE).withObject(new GetHealthStatusRequestDto()).build();
        final Session session = mock(Session.class);
        this.messageListener.onMessage(message, session);
    }

    @Then("I should connect to the IEC60870 device")
    public void thenIShouldConnectToTheIec60870Device() throws ConnectionFailureException {
        verify(this.client).connect(any(DeviceConnectionParameters.class), any(ConnectionEventListener.class));
    }

    @Then("I should cache the connection with the IEC60870 device")
    public void thenIShouldCacheTheConnection() {
        verify(this.cache).addConnection(eq(DEVICE_IDENTIFICATION), any(ClientConnection.class));
    }
}
