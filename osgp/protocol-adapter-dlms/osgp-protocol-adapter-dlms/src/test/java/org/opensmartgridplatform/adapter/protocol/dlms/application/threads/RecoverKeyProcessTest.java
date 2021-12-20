/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.threads;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.E_METER_AUTHENTICATION;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.E_METER_ENCRYPTION;

import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.DlmsConnection;
import org.opensmartgridplatform.adapter.protocol.dlms.application.config.ThrottlingClientConfig;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SecretManagementService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ThrottlingService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.Hls5Connector;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.RecoverKeyException;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class RecoverKeyProcessTest {

  private static final String DEVICE_IDENTIFICATION = "E000123456789";
  private static final String IP_ADDRESS = "1.1.1.1";

  @InjectMocks RecoverKeyProcess recoverKeyProcess;

  @Mock DomainHelperService domainHelperService;
  @Mock Hls5Connector hls5Connector;
  @Mock SecretManagementService secretManagementService;
  @Mock ThrottlingService throttlingService;
  @Mock DlmsDeviceRepository dlmsDeviceRepository;
  @Mock ThrottlingClientConfig throttlingClientConfig;

  @Mock DlmsDevice dlmsDevice;
  @Mock MessageMetadata messageMetadata;

  @BeforeEach
  public void before() {
    this.recoverKeyProcess.setDeviceIdentification(DEVICE_IDENTIFICATION);
    this.recoverKeyProcess.setIpAddress(IP_ADDRESS);
    this.recoverKeyProcess.setMessageMetadata(this.messageMetadata);
    lenient().when(this.throttlingClientConfig.clientEnabled()).thenReturn(false);
  }

  @Test
  void testWhenDeviceNotFoundThenException() throws OsgpException {

    // GIVEN
    when(this.domainHelperService.findDlmsDevice(DEVICE_IDENTIFICATION))
        .thenThrow(
            new FunctionalException(
                FunctionalExceptionType.UNKNOWN_DEVICE, ComponentType.PROTOCOL_DLMS));

    // WHEN
    assertThrows(RecoverKeyException.class, () -> this.recoverKeyProcess.run());

    // THEN
    verify(this.secretManagementService, never()).activateNewKeys(any(), any(), any());
  }

  @Test
  void testWhenNotAbleToConnectWithNewKeys() throws OsgpException, IOException {

    // GIVEN
    when(this.domainHelperService.findDlmsDevice(DEVICE_IDENTIFICATION))
        .thenReturn(this.dlmsDevice);
    when(this.hls5Connector.connectUnchecked(
            eq(this.messageMetadata), eq(this.dlmsDevice), any(), any()))
        .thenReturn(null);

    // WHEN
    this.recoverKeyProcess.run();

    // THEN
    verify(this.secretManagementService, never())
        .hasNewSecret(this.messageMetadata, DEVICE_IDENTIFICATION);
    verify(this.domainHelperService).findDlmsDevice(DEVICE_IDENTIFICATION);
    verify(this.secretManagementService, never()).activateNewKeys(any(), any(), any());
  }

  @Test
  void testThrottlingServiceCalledAndKeysActivated() throws Exception {

    // GIVEN
    when(this.domainHelperService.findDlmsDevice(DEVICE_IDENTIFICATION))
        .thenReturn(this.dlmsDevice);
    when(this.dlmsDevice.needsInvocationCounter()).thenReturn(true);
    when(this.hls5Connector.connectUnchecked(
            eq(this.messageMetadata), eq(this.dlmsDevice), any(), any()))
        .thenReturn(mock(DlmsConnection.class));

    // WHEN
    this.recoverKeyProcess.run();

    // THEN
    final InOrder inOrder = inOrder(this.throttlingService, this.hls5Connector);

    inOrder.verify(this.throttlingService).openConnection();
    inOrder
        .verify(this.hls5Connector)
        .connectUnchecked(eq(this.messageMetadata), eq(this.dlmsDevice), any(), any());
    inOrder.verify(this.throttlingService).closeConnection();

    verify(this.secretManagementService)
        .activateNewKeys(
            this.messageMetadata,
            DEVICE_IDENTIFICATION,
            Arrays.asList(E_METER_ENCRYPTION, E_METER_AUTHENTICATION));
    verify(this.dlmsDeviceRepository).save(this.dlmsDevice);
  }

  @Test
  void testWhenConnectionFailedThenConnectionClosedAtThrottlingService() throws Exception {

    // GIVEN
    when(this.domainHelperService.findDlmsDevice(DEVICE_IDENTIFICATION))
        .thenReturn(this.dlmsDevice);
    when(this.hls5Connector.connectUnchecked(any(), any(), any(), any())).thenReturn(null);

    // WHEN
    this.recoverKeyProcess.run();

    // THEN
    final InOrder inOrder = inOrder(this.throttlingService, this.hls5Connector);

    inOrder.verify(this.throttlingService).openConnection();
    inOrder.verify(this.hls5Connector).connectUnchecked(any(), any(), any(), any());
    inOrder.verify(this.throttlingService).closeConnection();

    verify(this.secretManagementService, never()).activateNewKeys(any(), any(), any());
  }

  @Test
  void setsIpAddressFromMessageMetadataIfIpAddressIsStatic() throws Exception {
    when(this.domainHelperService.findDlmsDevice(DEVICE_IDENTIFICATION))
        .thenReturn(this.dlmsDevice);
    when(this.dlmsDevice.isIpAddressIsStatic()).thenReturn(true);
    when(this.messageMetadata.getIpAddress()).thenReturn(IP_ADDRESS);

    this.recoverKeyProcess.run();

    verify(this.dlmsDevice).setIpAddress(IP_ADDRESS);
    verify(this.domainHelperService, never()).getDeviceIpAddressFromSessionProvider(any());
  }

  @Test
  void setsIpAddressFromSessionProviderIfIpAddressIsNotStatic() throws Exception {
    when(this.domainHelperService.findDlmsDevice(DEVICE_IDENTIFICATION))
        .thenReturn(this.dlmsDevice);
    when(this.dlmsDevice.isIpAddressIsStatic()).thenReturn(false);
    when(this.domainHelperService.getDeviceIpAddressFromSessionProvider(this.dlmsDevice))
        .thenReturn(IP_ADDRESS);

    this.recoverKeyProcess.run();

    verify(this.dlmsDevice).setIpAddress(IP_ADDRESS);
    verify(this.messageMetadata, never()).getIpAddress();
  }
}
