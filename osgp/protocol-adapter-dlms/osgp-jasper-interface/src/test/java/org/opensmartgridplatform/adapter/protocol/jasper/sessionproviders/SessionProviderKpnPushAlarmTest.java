// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.jasper.client.JasperWirelessSmsClient;
import org.opensmartgridplatform.adapter.protocol.jasper.exceptions.OsgpJasperException;
import org.opensmartgridplatform.adapter.protocol.jasper.service.DeviceSessionService;
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
  @Mock private DeviceSessionService deviceSessionService;
  private SessionProviderKpnPushAlarm sessionProviderKpnPushAlarm;

  @BeforeEach
  void setUp() {
    this.sessionProviderKpnPushAlarm =
        new SessionProviderKpnPushAlarm(
            this.sessionProviderMap, this.jasperWirelessSmsClient, this.deviceSessionService);
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
  }

  @Test
  void testGetIpAddressJasperException() throws OsgpJasperException {
    when(this.jasperWirelessSmsClient.sendWakeUpSMS(ICC_ID)).thenThrow(new OsgpJasperException(""));

    final FunctionalException functionalException =
        assertThrows(
            FunctionalException.class,
            () -> {
              this.sessionProviderKpnPushAlarm.getIpAddress(DEVICE_IDENTIFICATION, ICC_ID);
            });

    assertThat(functionalException.getExceptionType())
        .isEqualTo(FunctionalExceptionType.INVALID_ICCID);
    verifyNoInteractions(this.deviceSessionService);
  }
}
