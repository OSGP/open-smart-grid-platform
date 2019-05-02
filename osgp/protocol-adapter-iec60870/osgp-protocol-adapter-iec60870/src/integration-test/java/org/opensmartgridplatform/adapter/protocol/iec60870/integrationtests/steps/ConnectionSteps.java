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
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.TestDefaults.DEFAULT_DEVICE_IDENTIFICATION;

import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ConnectionEventListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.Client;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientAsduHandlerRegistry;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionCache;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionEventListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ConnectionParameters;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DomainInfo;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.shared.exceptionhandling.ConnectionFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class ConnectionSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionSteps.class);

    @Autowired
    private ClientConnectionCache connectionCacheSpy;

    @Autowired
    private Client clientMock;

    @Autowired
    private ClientAsduHandlerRegistry clientAsduHandlerRegistry;

    private ConnectionEventListener connectionEventListener;

    @Given("the IEC60870 device is not connected")
    public void givenIec60870DeviceIsNotConnected() throws ConnectionFailureException {
        LOGGER.debug("Given IEC60870 device is not connected");

        reset(this.connectionCacheSpy);

        // Make sure the client connect works as expected
        final DeviceConnection deviceConnection = new DeviceConnection(mock(Connection.class),
                new ConnectionParameters.Builder().deviceIdentification(DEFAULT_DEVICE_IDENTIFICATION).build());
        when(this.clientMock.connect(any(ConnectionParameters.class), any(ConnectionEventListener.class)))
                .thenReturn(deviceConnection);
    }

    @Given("an existing connection with an IEC60870 device")
    public void givenIec60870DeviceIsConnected() {
        LOGGER.debug("Given IEC60870 device is connected");

        final ConnectionParameters connectionParameters = new ConnectionParameters.Builder().commonAddress(0)
                .deviceIdentification("TEST-DEVICE").ipAddress("localhost").port(2404).build();
        final ResponseMetadata responseMetadata = new ResponseMetadata.Builder().withDeviceIdentification("TEST-DEVICE")
                .withOrganisationIdentification("TEST-ORGANISATION")
                .withDomainInfo(new DomainInfo("TEST-DOMAIN", "TEST_DOMAIN-VERSION"))
                .withMessageType("TEST-MESSAGE-TYPE").build();
        this.connectionEventListener = new ClientConnectionEventListener(connectionParameters.getDeviceIdentification(),
                this.connectionCacheSpy, this.clientAsduHandlerRegistry, responseMetadata);
    }

    @Then("I should connect to the IEC60870 device")
    public void thenIShouldConnectToIec60870Device() throws ConnectionFailureException {
        verify(this.clientMock).connect(any(ConnectionParameters.class), any(ConnectionEventListener.class));
    }

    @Then("I should cache the connection with the IEC60870 device")
    public void thenIShouldCacheConnection() {
        verify(this.connectionCacheSpy).addConnection(eq(DEFAULT_DEVICE_IDENTIFICATION), any(DeviceConnection.class));
    }

    public ConnectionEventListener getConnectionEventListener() {
        return this.connectionEventListener;
    }
}
