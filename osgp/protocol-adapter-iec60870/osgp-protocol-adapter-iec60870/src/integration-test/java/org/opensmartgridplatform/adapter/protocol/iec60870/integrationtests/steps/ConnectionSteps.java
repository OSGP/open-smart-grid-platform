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
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_DEVICE_IDENTIFICATION;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_DOMAIN;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_DOMAIN_VERSION;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_MESSAGE_TYPE;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_ORGANISATION_IDENTIFICATION;

import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ConnectionEventListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.exceptions.ClientConnectionAlreadyInCacheException;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.Client;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientAsduHandlerRegistry;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionCache;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionEventListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ConnectionParameters;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DomainInfo;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.shared.exceptionhandling.ConnectionFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class ConnectionSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionSteps.class);

    @Autowired
    private ClientConnectionService clientConnectionService;

    @Autowired
    private ClientConnectionCache connectionCacheSpy;

    @Autowired
    private Client clientMock;

    @Autowired
    private ClientAsduHandlerRegistry clientAsduHandlerRegistry;

    private ConnectionEventListener connectionEventListener;

    private ConnectionParameters connectionParameters;

    @Before
    public void setup() {
        // Make sure there is no connection in the cache
        this.clientConnectionService.closeAllConnections();

        this.connectionParameters = new ConnectionParameters.Builder().commonAddress(0)
                .deviceIdentification(DEFAULT_DEVICE_IDENTIFICATION)
                .ipAddress("localhost")
                .port(2404)
                .build();
    }

    @Given("the IEC60870 device is not connected")
    public void givenIec60870DeviceIsNotConnected() throws ConnectionFailureException {
        LOGGER.debug("Given IEC60870 device is not connected");

        // Make sure the client connect works as expected
        final DeviceConnection deviceConnection = new DeviceConnection(mock(Connection.class),
                this.connectionParameters);
        when(this.clientMock.connect(eq(this.connectionParameters), any(ClientConnectionEventListener.class)))
                .thenReturn(deviceConnection);
    }

    @Given("an existing connection with an IEC60870 device")
    public void givenIec60870DeviceIsConnected() throws ClientConnectionAlreadyInCacheException {
        LOGGER.debug("Given IEC60870 device is connected");

        // Make sure the connection event listener works as expected
        final ResponseMetadata responseMetadata = new ResponseMetadata.Builder()
                .withDeviceIdentification(DEFAULT_DEVICE_IDENTIFICATION)
                .withOrganisationIdentification(DEFAULT_ORGANISATION_IDENTIFICATION)
                .withDomainInfo(new DomainInfo(DEFAULT_DOMAIN, DEFAULT_DOMAIN_VERSION))
                .withMessageType(DEFAULT_MESSAGE_TYPE)
                .build();
        this.connectionEventListener = new ClientConnectionEventListener(
                this.connectionParameters.getDeviceIdentification(), this.connectionCacheSpy,
                this.clientAsduHandlerRegistry, responseMetadata);

        // Make sure a connection could be retrieved from the cache
        // Only needed for scenarios sending requests to a device
        final Connection connection = mock(Connection.class);
        this.connectionCacheSpy.addConnection(DEFAULT_DEVICE_IDENTIFICATION,
                new DeviceConnection(connection, this.connectionParameters));
    }

    @Then("I should connect to the IEC60870 device")
    public void thenIShouldConnectToIec60870Device() throws ConnectionFailureException {
        verify(this.clientMock).connect(eq(this.connectionParameters), any(ClientConnectionEventListener.class));
    }

    @Then("I should cache the connection with the IEC60870 device")
    public void thenIShouldCacheConnection() throws ClientConnectionAlreadyInCacheException {
        verify(this.connectionCacheSpy).addConnection(eq(DEFAULT_DEVICE_IDENTIFICATION), any(DeviceConnection.class));
    }

    public ConnectionEventListener getConnectionEventListener() {
        return this.connectionEventListener;
    }
}
