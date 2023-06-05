// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.steps;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_DEVICE_IDENTIFICATION;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_MESSAGE_TYPE;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_ORGANISATION_IDENTIFICATION;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ConnectionEventListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.exceptions.ClientConnectionAlreadyInCacheException;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.LogItemFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.ResponseMetadataFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.Client;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientAsduHandlerRegistry;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionCache;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionEventListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.LoggingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ConnectionParameters;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DomainInfo;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories.DomainInfoFactory;
import org.opensmartgridplatform.shared.exceptionhandling.ConnectionFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ConnectionSteps {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionSteps.class);

  @Autowired private ClientConnectionService clientConnectionService;

  @Autowired private ClientConnectionCache connectionCacheSpy;

  @Autowired private Client clientMock;

  @Autowired private ClientAsduHandlerRegistry clientAsduHandlerRegistry;

  @Autowired private Iec60870DeviceSteps iec60870DeviceSteps;

  @Autowired private Connection connection;

  @Autowired private LoggingService loggingService;

  @Autowired private LogItemFactory logItemFactory;

  @Autowired private ResponseMetadataFactory responseMetadataFactory;

  private ConnectionEventListener connectionEventListener;

  private ConnectionParameters connectionParameters;

  @Before
  public void setup() {
    // Make sure there is no connection in the cache
    this.clientConnectionService.closeAllConnections();
    this.connectionParameters = this.initConnectionParameters(DEFAULT_DEVICE_IDENTIFICATION);
  }

  @Given("the IEC60870 device is not connected")
  public void givenIec60870DeviceIsNotConnected() throws ConnectionFailureException {
    LOGGER.debug("Given IEC60870 device is not connected");

    // Make sure the client connect works as expected
    final DeviceConnection deviceConnection =
        new DeviceConnection(mock(Connection.class), this.connectionParameters);
    when(this.clientMock.connect(
            eq(this.connectionParameters), any(ClientConnectionEventListener.class)))
        .thenReturn(deviceConnection);
  }

  @Given("an existing connection with IEC60870 device {string} of type {deviceType}")
  public void givenIec60870DeviceIsConnected(
      final String deviceIdentification, final DeviceType deviceType) throws Exception {
    LOGGER.debug(
        "Given an existing connection with IEC60870 device {} of type {}",
        deviceIdentification,
        deviceType);
    // Make sure the connection event listener works as expected
    this.connectionParameters = this.initConnectionParameters(deviceIdentification);
    final ResponseMetadata responseMetadata =
        this.initResponseMetadata(deviceIdentification, deviceType);

    this.connectionEventListener =
        new ClientConnectionEventListener.Builder()
            .withDeviceIdentification(deviceIdentification)
            .withClientAsduHandlerRegistry(this.clientAsduHandlerRegistry)
            .withClientConnectionCache(this.connectionCacheSpy)
            .withLoggingService(this.loggingService)
            .withLogItemFactory(this.logItemFactory)
            .withResponseMetadata(responseMetadata)
            .withResponseMetadataFactory(this.responseMetadataFactory)
            .build();

    // Make sure a connection could be retrieved from the cache
    // Only needed for scenarios sending requests to a device
    // final Connection connection = mock(Connection.class);
    this.connectionCacheSpy.addConnection(
        deviceIdentification, new DeviceConnection(this.connection, this.connectionParameters));
  }

  @Given("an active connection with the light measurement rtu {string}")
  public void anActiveConnectionWithTheControlledStation(final String deviceIdentification)
      throws Exception {
    this.givenIec60870DeviceIsConnected(deviceIdentification, DeviceType.LIGHT_MEASUREMENT_RTU);
  }

  @When("I connect to IEC60870 device {string}")
  public void whenIConnectToIEC60870Device(final String deviceIdentification) throws Exception {
    LOGGER.debug("When I connect to IEC60870 device {}", deviceIdentification);
    final Iec60870Device device =
        this.iec60870DeviceSteps
            .getDevice(deviceIdentification)
            .orElseThrow(() -> new Exception("Device not found"));
    final DeviceType deviceType = device.getDeviceType();
    final String connectionDeviceIdentification = device.getConnectionDeviceIdentification();

    this.connectionParameters = this.initConnectionParameters(connectionDeviceIdentification);

    this.connectionEventListener =
        new ClientConnectionEventListener.Builder()
            .withDeviceIdentification(connectionDeviceIdentification)
            .withClientAsduHandlerRegistry(this.clientAsduHandlerRegistry)
            .withClientConnectionCache(this.connectionCacheSpy)
            .withLoggingService(this.loggingService)
            .withLogItemFactory(this.logItemFactory)
            .withResponseMetadata(this.initResponseMetadata(deviceIdentification, deviceType))
            .withResponseMetadataFactory(this.responseMetadataFactory)
            .build();

    when(this.clientMock.connect(
            any(ConnectionParameters.class), any(ConnectionEventListener.class)))
        .thenReturn(new DeviceConnection(this.connection, this.connectionParameters));
  }

  @Then("I should connect to the IEC60870 device")
  public void thenIShouldConnectToIec60870Device() throws ConnectionFailureException {
    verify(this.clientMock)
        .connect(eq(this.connectionParameters), any(ClientConnectionEventListener.class));
  }

  @Then("I should cache the connection with the IEC60870 device")
  public void thenIShouldCacheConnection() throws ClientConnectionAlreadyInCacheException {
    verify(this.connectionCacheSpy)
        .addConnection(eq(DEFAULT_DEVICE_IDENTIFICATION), any(DeviceConnection.class));
  }

  public ConnectionEventListener getConnectionEventListener() {
    return this.connectionEventListener;
  }

  private ConnectionParameters initConnectionParameters(final String deviceIdentification) {
    return new ConnectionParameters.Builder().deviceIdentification(deviceIdentification).build();
  }

  private ResponseMetadata initResponseMetadata(
      final String deviceIdentification, final DeviceType deviceType) throws Exception {
    final DomainInfo domainInfo = DomainInfoFactory.forDeviceType(deviceType);
    // Make sure the connection event listener works as expected
    final ResponseMetadata responseMetadata =
        new ResponseMetadata.Builder()
            .withDeviceIdentification(deviceIdentification)
            .withDeviceType(deviceType)
            .withOrganisationIdentification(DEFAULT_ORGANISATION_IDENTIFICATION)
            .withDomainInfo(domainInfo)
            .withMessageType(DEFAULT_MESSAGE_TYPE)
            .build();
    return responseMetadata;
  }

  public void prepareForConnect(final String deviceIdentification) throws Exception {
    this.whenIConnectToIEC60870Device(deviceIdentification);
  }
}
