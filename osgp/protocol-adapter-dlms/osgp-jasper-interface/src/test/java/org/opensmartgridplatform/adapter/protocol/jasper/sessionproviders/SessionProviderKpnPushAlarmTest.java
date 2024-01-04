// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.jasper.client.JasperWirelessSmsClient;
import org.opensmartgridplatform.adapter.protocol.jasper.client.JasperWirelessTerminalClient;
import org.opensmartgridplatform.adapter.protocol.jasper.response.GetSessionInfoResponse;
import org.opensmartgridplatform.adapter.protocol.jasper.service.DeviceSessionService;
import org.opensmartgridplatform.jasper.exceptions.OsgpJasperException;
import org.opensmartgridplatform.jasper.rest.JasperError;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

@ExtendWith(MockitoExtension.class)
public class SessionProviderKpnPushAlarmTest {

  private static final String DEVICE_IDENTIFICATION = "device-identification";
  private static final String ICC_ID = "icc-id";
  private static final String IP_ADDRESS = "1.2.3.4";

  @Mock private SessionProviderMap sessionProviderMap;
  @Mock private JasperWirelessSmsClient jasperWirelessSmsClient;
  @Mock private JasperWirelessTerminalClient jasperWirelessTerminalClient;

  @Mock private DeviceSessionService deviceSessionService;
  private final int nrOfAttempts = 2;
  private SessionProviderKpnPushAlarm sessionProviderKpnPushAlarm;

  @BeforeEach
  void setUp() {
    this.sessionProviderKpnPushAlarm =
        new SessionProviderKpnPushAlarm(
            this.sessionProviderMap,
            this.jasperWirelessSmsClient,
            this.jasperWirelessTerminalClient,
            this.deviceSessionService,
            this.nrOfAttempts);
  }

  @Test
  void testInit() {
    this.sessionProviderKpnPushAlarm.init();

    verify(this.sessionProviderMap)
        .addProvider(SessionProviderEnum.KPN, this.sessionProviderKpnPushAlarm);
  }

  @Test
  void testGetIpAddress() throws OsgpException, OsgpJasperException {
    when(this.deviceSessionService.waitForIpAddress(DEVICE_IDENTIFICATION))
        .thenReturn(Optional.of(IP_ADDRESS));

    final Optional<String> ipAddress =
        this.sessionProviderKpnPushAlarm.getIpAddress(DEVICE_IDENTIFICATION, ICC_ID);

    assertThat(ipAddress).isEqualTo(Optional.of(IP_ADDRESS));
    verify(this.jasperWirelessSmsClient).sendWakeUpSMS(ICC_ID);
    verifyNoInteractions(this.jasperWirelessTerminalClient);
  }

  @Test
  void testGetIpAddressNoAlarmIpFromSession() throws OsgpException, OsgpJasperException {
    when(this.deviceSessionService.waitForIpAddress(DEVICE_IDENTIFICATION))
        .thenReturn(Optional.empty());
    when(this.jasperWirelessTerminalClient.getSession(ICC_ID))
        .thenReturn(this.newGetSessionInfoResponse(IP_ADDRESS));

    final Optional<String> ipAddress =
        this.sessionProviderKpnPushAlarm.getIpAddress(DEVICE_IDENTIFICATION, ICC_ID);

    assertThat(ipAddress).isEqualTo(Optional.of(IP_ADDRESS));
    verify(this.jasperWirelessSmsClient).sendWakeUpSMS(ICC_ID);
  }

  @Test
  void testGetIpAddressNoAlarmNoSession() throws OsgpException, OsgpJasperException {
    when(this.deviceSessionService.waitForIpAddress(DEVICE_IDENTIFICATION))
        .thenReturn(Optional.empty())
        .thenReturn(Optional.empty());
    when(this.jasperWirelessTerminalClient.getSession(ICC_ID))
        .thenReturn(this.newGetSessionInfoResponse(null))
        .thenReturn(this.newGetSessionInfoResponse(null));

    final Optional<String> ipAddress =
        this.sessionProviderKpnPushAlarm.getIpAddress(DEVICE_IDENTIFICATION, ICC_ID);

    assertThat(ipAddress).isEqualTo(Optional.empty());
    verify(this.jasperWirelessSmsClient).sendWakeUpSMS(ICC_ID);
  }

  @Test
  void testGetIpAddressSecondAttemptAlarm() throws OsgpException, OsgpJasperException {
    when(this.deviceSessionService.waitForIpAddress(DEVICE_IDENTIFICATION))
        .thenReturn(Optional.empty())
        .thenReturn(Optional.of(IP_ADDRESS));
    when(this.jasperWirelessTerminalClient.getSession(ICC_ID))
        .thenReturn(this.newGetSessionInfoResponse(null));

    final Optional<String> ipAddress =
        this.sessionProviderKpnPushAlarm.getIpAddress(DEVICE_IDENTIFICATION, ICC_ID);

    assertThat(ipAddress).isEqualTo(Optional.of(IP_ADDRESS));
    verify(this.jasperWirelessSmsClient).sendWakeUpSMS(ICC_ID);
  }

  @Test
  void testGetIpAddressSecondAttemptSession() throws OsgpException, OsgpJasperException {
    when(this.deviceSessionService.waitForIpAddress(DEVICE_IDENTIFICATION))
        .thenReturn(Optional.empty())
        .thenReturn(Optional.empty());
    when(this.jasperWirelessTerminalClient.getSession(ICC_ID))
        .thenReturn(this.newGetSessionInfoResponse(null))
        .thenReturn(this.newGetSessionInfoResponse(IP_ADDRESS));

    final Optional<String> ipAddress =
        this.sessionProviderKpnPushAlarm.getIpAddress(DEVICE_IDENTIFICATION, ICC_ID);

    assertThat(ipAddress).isEqualTo(Optional.of(IP_ADDRESS));
    verify(this.jasperWirelessSmsClient).sendWakeUpSMS(ICC_ID);
  }

  @Test
  void testGetIpAddressJasperExceptionNotFound() throws OsgpJasperException {
    when(this.jasperWirelessSmsClient.sendWakeUpSMS(ICC_ID))
        .thenThrow(new OsgpJasperException(JasperError.INVALID_ICCID));

    final FunctionalException functionalException =
        assertThrows(
            FunctionalException.class,
            () -> {
              this.sessionProviderKpnPushAlarm.getIpAddress(DEVICE_IDENTIFICATION, ICC_ID);
            });

    assertThat(functionalException.getExceptionType())
        .isEqualTo(FunctionalExceptionType.INVALID_ICCID);
    assertThat(functionalException.getComponentType()).isEqualTo(ComponentType.PROTOCOL_DLMS);
    verifyNoInteractions(this.deviceSessionService);
  }

  @Test
  void testGetIpAddressJasperExceptionOtherError() throws OsgpJasperException {
    when(this.jasperWirelessSmsClient.sendWakeUpSMS(ICC_ID))
        .thenThrow(new OsgpJasperException(JasperError.INVALID_REQUEST));

    final FunctionalException functionalException =
        assertThrows(
            FunctionalException.class,
            () -> {
              this.sessionProviderKpnPushAlarm.getIpAddress(DEVICE_IDENTIFICATION, ICC_ID);
            });

    assertThat(functionalException.getExceptionType())
        .isEqualTo(FunctionalExceptionType.SESSION_PROVIDER_ERROR);
    assertThat(functionalException.getComponentType()).isEqualTo(ComponentType.PROTOCOL_DLMS);
    verifyNoInteractions(this.deviceSessionService);
  }

  @Test
  void testGetIpAddressJasperExceptionString() throws OsgpJasperException {
    final String errorMessage = "error message";
    when(this.jasperWirelessSmsClient.sendWakeUpSMS(ICC_ID))
        .thenThrow(new OsgpJasperException("String error"));

    final FunctionalException functionalException =
        assertThrows(
            FunctionalException.class,
            () -> {
              this.sessionProviderKpnPushAlarm.getIpAddress(DEVICE_IDENTIFICATION, ICC_ID);
            });

    assertThat(functionalException.getExceptionType())
        .isEqualTo(FunctionalExceptionType.SESSION_PROVIDER_ERROR);
    assertThat(functionalException.getComponentType()).isEqualTo(ComponentType.PROTOCOL_DLMS);
    verifyNoInteractions(this.deviceSessionService);
  }

  private GetSessionInfoResponse newGetSessionInfoResponse(final String ipAddress) {
    return new GetSessionInfoResponse(ICC_ID, ipAddress, null, new Date(), new Date());
  }
}
