/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDeviceBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.jasper.infra.ws.JasperWirelessSmsClient;
import org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders.SessionProvider;
import org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders.SessionProviderService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DomainHelperServiceTest {

  @Mock private DlmsDeviceRepository dlmsDeviceRepository;
  @Mock private SessionProviderService sessionProviderService;
  @Mock private JasperWirelessSmsClient jasperWirelessSmsClient;
  private final int jasperGetSessionRetries = 1;
  private final int jasperGetSessionSleepBetweenRetries = 2;

  private DomainHelperService domainHelperService;

  private static final String DEVICE_IDENTIFICATION = "E000123456789";
  private static final String IP_ADDRESS = "1.1.1.1";

  @BeforeEach
  void setUp() {
    this.domainHelperService =
        new DomainHelperService(
            this.dlmsDeviceRepository,
            this.sessionProviderService,
            this.jasperWirelessSmsClient,
            this.jasperGetSessionRetries,
            this.jasperGetSessionSleepBetweenRetries);
  }

  @Test
  void findDlmsDeviceMetadataNotFound() {
    final MessageMetadata messageMetadata = mock(MessageMetadata.class);
    when(messageMetadata.getDeviceIdentification()).thenReturn(DEVICE_IDENTIFICATION);

    when(this.dlmsDeviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION))
        .thenReturn(null);

    final FunctionalException exception =
        assertThrows(
            FunctionalException.class,
            () -> {
              this.domainHelperService.findDlmsDevice(messageMetadata);
            });
    assertThat(exception.getExceptionType()).isEqualTo(FunctionalExceptionType.UNKNOWN_DEVICE);
    assertThat(exception.getComponentType()).isEqualTo(ComponentType.PROTOCOL_DLMS);
  }

  @Test
  void findDlmsDeviceMetadataIpAddressStatic() throws OsgpException {
    final MessageMetadata messageMetadata = mock(MessageMetadata.class);
    when(messageMetadata.getDeviceIdentification()).thenReturn(DEVICE_IDENTIFICATION);
    when(messageMetadata.getIpAddress()).thenReturn(IP_ADDRESS);

    final DlmsDevice device = new DlmsDeviceBuilder().withIpAddressStatic(true).build();
    when(this.dlmsDeviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION))
        .thenReturn(device);

    final DlmsDevice foundDevice = this.domainHelperService.findDlmsDevice(messageMetadata);

    assertThat(foundDevice.getIpAddress()).isEqualTo(IP_ADDRESS);
  }

  @Test
  void findDlmsDeviceMetadataIpNotStatic() throws OsgpException {
    final MessageMetadata messageMetadata = mock(MessageMetadata.class);
    when(messageMetadata.getDeviceIdentification()).thenReturn(DEVICE_IDENTIFICATION);

    final DlmsDevice device =
        new DlmsDeviceBuilder()
            .withIpAddressStatic(false)
            .withCommunicationMethod("CDMA")
            .setIccId("ICC ID")
            .build();
    when(this.dlmsDeviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION))
        .thenReturn(device);

    final SessionProvider sessionProvider = mock(SessionProvider.class);
    when(this.sessionProviderService.getSessionProvider(device.getCommunicationProvider()))
        .thenReturn(sessionProvider);

    final String ipAddressFromSession = "2.2.2.2";
    when(sessionProvider.getIpAddress(device.getIccId())).thenReturn(ipAddressFromSession);

    final DlmsDevice foundDevice = this.domainHelperService.findDlmsDevice(messageMetadata);

    assertThat(foundDevice.getIpAddress()).isEqualTo(ipAddressFromSession);
  }

  @Test
  void findDlmsDeviceNotFound() {
    when(this.dlmsDeviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION))
        .thenReturn(null);

    final FunctionalException exception =
        assertThrows(
            FunctionalException.class,
            () -> {
              this.domainHelperService.findDlmsDevice(DEVICE_IDENTIFICATION, IP_ADDRESS);
            });
    assertThat(exception.getExceptionType()).isEqualTo(FunctionalExceptionType.UNKNOWN_DEVICE);
    assertThat(exception.getComponentType()).isEqualTo(ComponentType.PROTOCOL_DLMS);
  }

  @Test
  void findDlmsDeviceIpAddressStatic() throws OsgpException {
    final DlmsDevice device = new DlmsDeviceBuilder().withIpAddressStatic(true).build();
    when(this.dlmsDeviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION))
        .thenReturn(device);

    final DlmsDevice foundDevice =
        this.domainHelperService.findDlmsDevice(DEVICE_IDENTIFICATION, IP_ADDRESS);

    assertThat(foundDevice.getIpAddress()).isEqualTo(IP_ADDRESS);
  }
}
