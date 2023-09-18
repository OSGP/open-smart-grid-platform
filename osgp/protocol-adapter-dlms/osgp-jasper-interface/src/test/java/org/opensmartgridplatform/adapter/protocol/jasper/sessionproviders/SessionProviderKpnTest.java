// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.shared.exceptionhandling.ComponentType.PROTOCOL_DLMS;
import static org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType.INVALID_ICCID;

import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.jasper.client.JasperWirelessSmsClient;
import org.opensmartgridplatform.adapter.protocol.jasper.client.JasperWirelessTerminalClient;
import org.opensmartgridplatform.adapter.protocol.jasper.exceptions.OsgpJasperException;
import org.opensmartgridplatform.adapter.protocol.jasper.response.GetSessionInfoResponse;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

@ExtendWith(MockitoExtension.class)
public class SessionProviderKpnTest {

  private static final String ICC_ID = "icc-id";
  private static final String IP_ADDRESS = "1.2.3.4";

  @Mock private SessionProviderMap sessionProviderMap;
  @Mock private JasperWirelessTerminalClient jasperWirelessTerminalClient;
  @Mock private JasperWirelessSmsClient jasperWirelessSmsClient;
  private final int jasperGetSessionRetries = 1;
  private final int jasperGetSessionSleepBetweenRetries = 2;

  private SessionProviderKpn sessionProviderKpn;

  @BeforeEach
  void setUp() {
    this.sessionProviderKpn =
        new SessionProviderKpn(
            this.sessionProviderMap,
            this.jasperWirelessTerminalClient,
            this.jasperWirelessSmsClient,
            this.jasperGetSessionRetries,
            this.jasperGetSessionSleepBetweenRetries);
  }

  @Test
  void testInit() {
    this.sessionProviderKpn.init();

    verify(this.sessionProviderMap).addProvider(SessionProviderEnum.KPN, this.sessionProviderKpn);
  }

  @Test
  void testGetIpAddressInSession() throws OsgpException, OsgpJasperException {
    final GetSessionInfoResponse response = this.newGetSessionInfoResponse(ICC_ID, IP_ADDRESS);
    when(this.jasperWirelessTerminalClient.getSession(ICC_ID)).thenReturn(response);

    final Optional<String> ipAddress = this.sessionProviderKpn.getIpAddress(ICC_ID);
    assertThat(ipAddress).isPresent().isEqualTo(Optional.of(IP_ADDRESS));
  }

  @Test
  void testGetIpAddressNotInSession() throws OsgpException, OsgpJasperException {
    final GetSessionInfoResponse emptyResponse = this.newGetSessionInfoResponse(ICC_ID, null);
    final GetSessionInfoResponse response = this.newGetSessionInfoResponse(ICC_ID, IP_ADDRESS);
    when(this.jasperWirelessTerminalClient.getSession(ICC_ID))
        .thenReturn(emptyResponse)
        .thenReturn(response);

    final Optional<String> ipAddress = this.sessionProviderKpn.getIpAddress(ICC_ID);

    verify(this.jasperWirelessSmsClient).sendWakeUpSMS(ICC_ID);
    assertThat(ipAddress).isPresent().isEqualTo(Optional.of(IP_ADDRESS));
  }

  @Test
  void testGetIpAddressNotInSessionMaxRetries() throws OsgpException, OsgpJasperException {
    final GetSessionInfoResponse emptyResponse = this.newGetSessionInfoResponse(ICC_ID, null);
    final GetSessionInfoResponse response = this.newGetSessionInfoResponse(ICC_ID, IP_ADDRESS);
    when(this.jasperWirelessTerminalClient.getSession(ICC_ID))
        .thenReturn(emptyResponse)
        .thenReturn(emptyResponse);

    final Optional<String> ipAddress = this.sessionProviderKpn.getIpAddress(ICC_ID);

    verify(this.jasperWirelessSmsClient).sendWakeUpSMS(ICC_ID);
    assertThat(ipAddress).isEmpty();
  }

  @Test
  void testGetIpAddressNotInSessionJasperException() throws OsgpJasperException {
    final GetSessionInfoResponse emptyResponse = this.newGetSessionInfoResponse(ICC_ID, null);
    final GetSessionInfoResponse response = this.newGetSessionInfoResponse(ICC_ID, IP_ADDRESS);
    when(this.jasperWirelessTerminalClient.getSession(ICC_ID))
        .thenReturn(emptyResponse)
        .thenReturn(response);

    when(this.jasperWirelessSmsClient.sendWakeUpSMS(ICC_ID)).thenThrow(new OsgpJasperException(""));

    final FunctionalException functionalException =
        assertThrows(
            FunctionalException.class,
            () -> {
              this.sessionProviderKpn.getIpAddress(ICC_ID);
            });

    assertThat(functionalException.getExceptionType()).isEqualTo(INVALID_ICCID);
    assertThat(functionalException.getComponentType()).isEqualTo(PROTOCOL_DLMS);
  }

  private GetSessionInfoResponse newGetSessionInfoResponse(
      final String iccId, final String ipAddress) {
    return new GetSessionInfoResponse(iccId, ipAddress, ipAddress, new Date(), new Date());
  }
}
