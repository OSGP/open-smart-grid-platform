/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.application.config.DevicePingConfig;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDeviceBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.InvocationCountingDlmsMessageListener;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.networking.ping.Pinger;

@ExtendWith(MockitoExtension.class)
class DlmsConnectionHelperTest {
  private DlmsConnectionHelper helper;
  private MessageMetadata messageMetadata;
  private Consumer<DlmsConnectionManager> task;

  @Mock private InvocationCounterManager invocationCounterManager;

  @Mock private DlmsConnectionFactory connectionFactory;

  @Mock private DevicePingConfig devicePingConfig;

  @Mock private Pinger pinger;

  @BeforeEach
  void setUp() {
    this.helper =
        new DlmsConnectionHelper(
            this.invocationCounterManager, this.connectionFactory, this.devicePingConfig, 0);
    this.messageMetadata = MessageMetadata.newBuilder().withCorrelationUid("123456").build();
    this.task = dlmsConnectionManager -> {};
  }

  @Test
  void pingsDeviceWithIpAddressBeforeCreatingConnectionIfPingingIsEnabled() throws Exception {
    final String deviceIpAddress = "192.168.92.56";
    when(this.devicePingConfig.pingingEnabled()).thenReturn(true);
    when(this.devicePingConfig.pinger()).thenReturn(this.pinger);
    final DlmsDevice device = new DlmsDeviceBuilder().withHls5Active(true).build();
    device.setIpAddress(deviceIpAddress);
    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();

    this.helper.createAndHandleConnectionForDevice(
        this.messageMetadata, device, listener, this.task);

    verify(this.pinger).ping(deviceIpAddress);
  }

  @Test
  void doesNotPingDeviceWithoutIpAddressBeforeCreatingConnectionIfPingingIsEnabled()
      throws Exception {
    when(this.devicePingConfig.pingingEnabled()).thenReturn(true);
    final DlmsDevice device = new DlmsDeviceBuilder().withHls5Active(true).build();
    device.setIpAddress(null);
    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();

    this.helper.createAndHandleConnectionForDevice(
        this.messageMetadata, device, listener, this.task);

    verifyNoInteractions(this.pinger);
    verifyNoMoreInteractions(this.devicePingConfig);
  }

  @Test
  void doesNotPingDeviceBeforeCreatingConnectionIfPingingIsDisabled() throws Exception {
    final String deviceIpAddress = "192.168.92.56";
    when(this.devicePingConfig.pingingEnabled()).thenReturn(false);
    final DlmsDevice device = new DlmsDeviceBuilder().withHls5Active(true).build();
    device.setIpAddress(deviceIpAddress);
    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();

    this.helper.createAndHandleConnectionForDevice(
        this.messageMetadata, device, listener, this.task);

    verifyNoInteractions(this.pinger);
    verifyNoMoreInteractions(this.devicePingConfig);
  }

  @Test
  void noInteractionsWithInvocationCounterManagerForDeviceThatDoesNotNeedInvocationCounter()
      throws Exception {
    final DlmsDevice device = new DlmsDeviceBuilder().withHls5Active(false).build();
    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();

    doNothing()
        .when(this.connectionFactory)
        .createAndHandleConnection(this.messageMetadata, device, listener, null, this.task);

    this.helper.createAndHandleConnectionForDevice(
        this.messageMetadata, device, listener, this.task);

    verifyNoInteractions(this.invocationCounterManager);
  }

  @Test
  void
      initializesInvocationCounterForDeviceThatNeedsInvocationCounterWithInvocationCounterUninitialized()
          throws Exception {
    final DlmsDevice device =
        new DlmsDeviceBuilder()
            .withHls5Active(true)
            .withProtocol("SMR")
            .withInvocationCounter(null)
            .build();
    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();

    doNothing()
        .when(this.connectionFactory)
        .createAndHandleConnection(this.messageMetadata, device, listener, null, this.task);

    this.helper.createAndHandleConnectionForDevice(
        this.messageMetadata, device, listener, this.task);

    verify(this.invocationCounterManager).initializeInvocationCounter(this.messageMetadata, device);
  }

  @Test
  void createsConnectionForDeviceThatNeedsInvocationCounterWithInvocationCounterInitialized()
      throws Exception {
    final DlmsDevice device =
        new DlmsDeviceBuilder().withHls5Active(true).withInvocationCounter(123L).build();
    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();

    doNothing()
        .when(this.connectionFactory)
        .createAndHandleConnection(this.messageMetadata, device, listener, null, this.task);

    this.helper.createAndHandleConnectionForDevice(
        this.messageMetadata, device, listener, this.task);

    verifyNoMoreInteractions(this.invocationCounterManager);
  }

  @Test
  void initializesInvocationCounterWhenInvocationCounterIsOutOfSyncForIskraDevice()
      throws Exception {
    final DlmsDevice device =
        new DlmsDeviceBuilder()
            .withHls5Active(true)
            .withProtocol("SMR")
            .withInvocationCounter(123L)
            .build();
    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();

    final ConnectionException exception =
        new ConnectionException(
            "Error creating connection for device E0033006878667817 with Ip address:62.133.86.119 Port:4059 "
                + "UseHdlc:false UseSn:false Message:UNKNOWN: Received an association response (AARE) with an"
                + " error message. Result name REJECTED_PERMANENT. Assumed fault: user.");
    doThrow(exception)
        .when(this.connectionFactory)
        .createAndHandleConnection(this.messageMetadata, device, listener, null, this.task);

    assertThrows(
        ConnectionException.class,
        () ->
            this.helper.createAndHandleConnectionForDevice(
                this.messageMetadata, device, listener, null, this.task));

    verify(this.invocationCounterManager).initializeInvocationCounter(this.messageMetadata, device);
    verify(this.connectionFactory, times(2))
        .createAndHandleConnection(this.messageMetadata, device, listener, null, this.task);
  }

  @Test
  void initializesInvocationCounterWhenInvocationCounterIsOutOfSyncForLAndGDevice()
      throws Exception {
    final DlmsDevice device =
        new DlmsDeviceBuilder()
            .withHls5Active(true)
            .withProtocol("SMR")
            .withInvocationCounter(123L)
            .build();
    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();

    final ConnectionException exception =
        new ConnectionException(
            "Error creating connection for device E0051004228715518 with Ip address:62.133.88.34 Port:null "
                + "UseHdlc:false UseSn:false Message:Socket was closed by remote host.");
    doThrow(exception)
        .when(this.connectionFactory)
        .createAndHandleConnection(this.messageMetadata, device, listener, null, this.task);

    assertThrows(
        ConnectionException.class,
        () ->
            this.helper.createAndHandleConnectionForDevice(
                this.messageMetadata, device, listener, null, this.task));

    verify(this.invocationCounterManager).initializeInvocationCounter(this.messageMetadata, device);
    verify(this.connectionFactory, times(2))
        .createAndHandleConnection(this.messageMetadata, device, listener, null, this.task);
  }

  @Test
  void invocationCounterUpdateSuccesfull() throws Exception {
    final DlmsDevice device =
        new DlmsDeviceBuilder()
            .withHls5Active(true)
            .withProtocol("SMR")
            .withInvocationCounter(123L)
            .build();
    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();

    final ConnectionException exception =
        new ConnectionException(
            "Error creating connection for device E0051004228715518 with Ip address:62.133.88.34 Port:null "
                + "UseHdlc:false UseSn:false Message:Socket was closed by remote host.");

    // First try throw exception, second time no exception
    doThrow(exception)
        .doNothing()
        .when(this.connectionFactory)
        .createAndHandleConnection(this.messageMetadata, device, listener, null, this.task);

    this.helper.createAndHandleConnectionForDevice(
        this.messageMetadata, device, listener, null, this.task);

    verify(this.invocationCounterManager).initializeInvocationCounter(this.messageMetadata, device);
    verify(this.connectionFactory, times(2))
        .createAndHandleConnection(this.messageMetadata, device, listener, null, this.task);
  }

  @Test
  void
      doesNotResetInvocationCounterWhenInvocationCounterIsOutOfSyncForDeviceThatNeedsNoInvocationCounter()
          throws Exception {
    final DlmsDevice device =
        new DlmsDeviceBuilder()
            .withHls5Active(true)
            .withProtocol("DSMR")
            .withInvocationCounter(123L)
            .build();
    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();

    final ConnectionException exception =
        new ConnectionException(
            "Error creating connection for device E0051004228715518 with Ip address:62.133.88.34 Port:null "
                + "UseHdlc:false UseSn:false Message:Socket was closed by remote host.");
    doThrow(exception)
        .when(this.connectionFactory)
        .createAndHandleConnection(this.messageMetadata, device, listener, null, this.task);

    assertThrows(
        ConnectionException.class,
        () ->
            this.helper.createAndHandleConnectionForDevice(
                this.messageMetadata, device, listener, this.task));

    verifyNoMoreInteractions(this.invocationCounterManager);
  }
}
