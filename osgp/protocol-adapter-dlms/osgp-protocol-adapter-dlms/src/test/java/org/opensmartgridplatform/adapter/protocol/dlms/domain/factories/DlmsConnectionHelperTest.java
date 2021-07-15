/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
import org.opensmartgridplatform.shared.infra.networking.ping.Pinger;

@ExtendWith(MockitoExtension.class)
class DlmsConnectionHelperTest {
  private DlmsConnectionHelper helper;

  @Mock private InvocationCounterManager invocationCounterManager;

  @Mock private DlmsConnectionFactory connectionFactory;

  @Mock private DevicePingConfig devicePingConfig;

  @Mock private Pinger pinger;

  @BeforeEach
  void setUp() {
    this.helper =
        new DlmsConnectionHelper(
            this.invocationCounterManager, this.connectionFactory, this.devicePingConfig);
  }

  @Test
  void pingsDeviceWithIpAddressBeforeCreatingConnectionIfPingingIsEnabled() throws Exception {
    final String deviceIpAddress = "192.168.92.56";
    when(this.devicePingConfig.pingingEnabled()).thenReturn(true);
    when(this.devicePingConfig.pinger()).thenReturn(this.pinger);
    final DlmsDevice device = new DlmsDeviceBuilder().withHls5Active(true).build();
    device.setIpAddress(deviceIpAddress);
    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();

    this.helper.createConnectionForDevice(device, listener);

    verify(this.pinger).ping(deviceIpAddress);
  }

  @Test
  void doesNotPingDeviceWithoutIpAddressBeforeCreatingConnectionIfPingingIsEnabled()
      throws Exception {
    final String noIpAddress = null;
    when(this.devicePingConfig.pingingEnabled()).thenReturn(true);
    final DlmsDevice device = new DlmsDeviceBuilder().withHls5Active(true).build();
    device.setIpAddress(noIpAddress);
    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();

    this.helper.createConnectionForDevice(device, listener);

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

    this.helper.createConnectionForDevice(device, listener);

    verifyNoInteractions(this.pinger);
    verifyNoMoreInteractions(this.devicePingConfig);
  }

  @Test
  void createsConnectionForDeviceThatDoesNotNeedInvocationCounter() throws Exception {
    final DlmsDevice device = new DlmsDeviceBuilder().withHls5Active(false).build();
    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();

    final DlmsConnectionManager connectionManager = mock(DlmsConnectionManager.class);
    when(this.connectionFactory.getConnection(device, listener)).thenReturn(connectionManager);

    final DlmsConnectionManager result = this.helper.createConnectionForDevice(device, listener);

    assertThat(result).isSameAs(connectionManager);
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

    final DlmsConnectionManager connectionManager = mock(DlmsConnectionManager.class);
    when(this.connectionFactory.getConnection(device, listener)).thenReturn(connectionManager);

    this.helper.createConnectionForDevice(device, listener);

    verify(this.invocationCounterManager).initializeInvocationCounter(device);
  }

  @Test
  void createsConnectionForDeviceThatNeedsInvocationCounterWithInvocationCounterInitialized()
      throws Exception {
    final DlmsDevice device =
        new DlmsDeviceBuilder().withHls5Active(true).withInvocationCounter(123L).build();
    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();

    final DlmsConnectionManager connectionManager = mock(DlmsConnectionManager.class);
    when(this.connectionFactory.getConnection(device, listener)).thenReturn(connectionManager);

    final DlmsConnectionManager result = this.helper.createConnectionForDevice(device, listener);

    assertThat(result).isSameAs(connectionManager);

    verifyNoMoreInteractions(this.invocationCounterManager);
  }

  @Test
  void resetsInvocationCounterWhenInvocationCounterIsOutOfSyncForIskraDevice() throws Exception {
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
    doThrow(exception).when(this.connectionFactory).getConnection(device, listener);

    try {
      this.helper.createConnectionForDevice(device, listener);
      fail("Expected ConnectionException");
    } catch (final ConnectionException e) {
      // expected
    }

    verify(this.invocationCounterManager).resetInvocationCounter(device);
  }

  @Test
  void resetsInvocationCounterWhenInvocationCounterIsOutOfSyncForLAndGDevice() throws Exception {
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
    doThrow(exception).when(this.connectionFactory).getConnection(device, listener);

    try {
      this.helper.createConnectionForDevice(device, listener);
      fail("Expected ConnectionException");
    } catch (final ConnectionException e) {
      // expected
    }

    verify(this.invocationCounterManager).resetInvocationCounter(device);
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
    doThrow(exception).when(this.connectionFactory).getConnection(device, listener);

    try {
      this.helper.createConnectionForDevice(device, listener);
      fail("Expected ConnectionException");
    } catch (final ConnectionException e) {
      // expected
    }

    verifyNoMoreInteractions(this.invocationCounterManager);
  }
}
