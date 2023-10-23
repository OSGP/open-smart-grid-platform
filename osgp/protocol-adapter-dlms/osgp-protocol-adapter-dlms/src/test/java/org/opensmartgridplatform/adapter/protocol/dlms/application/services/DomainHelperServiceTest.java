// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDeviceBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.jasper.rest.client.JasperWirelessSmsRestClient;
import org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders.SessionProvider;
import org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders.SessionProviderService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class DomainHelperServiceTest {

  @Mock private DlmsDeviceRepository dlmsDeviceRepository;
  @Mock private SessionProviderService sessionProviderService;
  @Mock private SessionProvider sessionProvider;
  @Mock private JasperWirelessSmsRestClient jasperWirelessSmsRestClient;

  private DomainHelperService domainHelperService;

  private static final String DEVICE_IDENTIFICATION = "E000123456789";
  private static final String IP_ADDRESS = "1.1.1.1";

  @BeforeEach
  void setUp() {
    this.domainHelperService =
        new DomainHelperService(this.dlmsDeviceRepository, this.sessionProviderService);
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
  void getDeviceIpAddressFromSessionProviderReturnsAddressFromProviderIfDeviceIsInSession()
      throws Exception {

    final String communicationProvider = "comm-prov";
    final String iccId = "icc-id";
    final String ipAddress = IP_ADDRESS;
    this.whenSessionProviderReturnsIpAddress(communicationProvider, iccId, ipAddress);
    final DlmsDevice dlmsDevice =
        new DlmsDeviceBuilder()
            .withDeviceIdentification(DEVICE_IDENTIFICATION)
            .withCommunicationProvider(communicationProvider)
            .setIccId(iccId)
            .build();

    final String actualIpAddress =
        this.domainHelperService.getDeviceIpAddressFromSessionProvider(dlmsDevice);

    assertThat(actualIpAddress).isEqualTo(ipAddress);
  }

  private void whenSessionProviderReturnsIpAddress(
      final String communicationProvider, final String iccId, final String ipAddress)
      throws Exception {

    when(this.sessionProviderService.getSessionProvider(communicationProvider))
        .thenReturn(this.sessionProvider);
    when(this.sessionProvider.getIpAddress(DEVICE_IDENTIFICATION, iccId))
        .thenReturn(Optional.of(IP_ADDRESS));
  }

  @Test
  void setsIpAddressFromMessageMetadataIfIpAddressIsStatic() throws Exception {
    final String ipAddress = IP_ADDRESS;
    final DlmsDevice dlmsDevice =
        new DlmsDeviceBuilder().withIpAddress(null).withIpAddressStatic(true).build();
    final MessageMetadata messageMetadata =
        MessageMetadata.newBuilder().withNetworkAddress(ipAddress).build();

    this.domainHelperService.setIpAddressFromMessageMetadataOrSessionProvider(
        dlmsDevice, messageMetadata);

    assertThat(dlmsDevice.getIpAddress()).isEqualTo(ipAddress);
  }

  @Test
  void setsIpAddressFromSessionProviderIfIpAddressIsNotStatic() throws Exception {
    final String communicationProvider = "comm-prov";
    final String iccId = "icc-id";
    final String ipAddress = IP_ADDRESS;

    when(this.sessionProviderService.getSessionProvider(communicationProvider))
        .thenReturn(this.sessionProvider);
    when(this.sessionProvider.getIpAddress(DEVICE_IDENTIFICATION, iccId))
        .thenReturn(Optional.of(ipAddress));

    final DlmsDevice dlmsDevice =
        new DlmsDeviceBuilder()
            .withDeviceIdentification(DEVICE_IDENTIFICATION)
            .withIpAddressStatic(false)
            .withCommunicationProvider(communicationProvider)
            .setIccId(iccId)
            .build();
    final MessageMetadata messageMetadata =
        MessageMetadata.newBuilder().withNetworkAddress(null).build();

    this.domainHelperService.setIpAddressFromMessageMetadataOrSessionProvider(
        dlmsDevice, messageMetadata);

    assertThat(dlmsDevice.getIpAddress()).isEqualTo(ipAddress);
  }

  @Test
  void doesNotSetIpAddressWhenItIsAlreadySetInDlmsDevice() throws Exception {
    final String ipAddressInMessageMetaData = "2.2.2.2";
    final DlmsDevice dlmsDevice =
        new DlmsDeviceBuilder().withIpAddress(IP_ADDRESS).withIpAddressStatic(true).build();
    final MessageMetadata messageMetadata =
        MessageMetadata.newBuilder().withNetworkAddress(ipAddressInMessageMetaData).build();

    this.domainHelperService.setIpAddressFromMessageMetadataOrSessionProvider(
        dlmsDevice, messageMetadata);

    assertThat(dlmsDevice.getIpAddress()).isEqualTo(IP_ADDRESS);
  }
}
