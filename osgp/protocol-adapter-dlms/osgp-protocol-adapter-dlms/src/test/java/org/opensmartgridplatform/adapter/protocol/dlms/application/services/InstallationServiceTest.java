// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.application.mapping.InstallationMapper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.security.RsaEncrypter;

@ExtendWith(MockitoExtension.class)
class InstallationServiceTest {

  private final MessageMetadata messageMetadata =
      MessageMetadata.newBuilder().withCorrelationUid("123456").build();
  private final String deviceIdentification = "Test";

  @InjectMocks InstallationService testService;
  @Mock SecretManagementService secretManagementService;
  @Mock DlmsDeviceRepository dlmsDeviceRepository;
  @Mock InstallationMapper installationMapper;
  @Mock RsaEncrypter rsaEncrypter;

  @Test
  void addEMeter() throws FunctionalException {
    // GIVEN
    final SmartMeteringDeviceDto deviceDto = new SmartMeteringDeviceDto();
    deviceDto.setDeviceIdentification(this.deviceIdentification);
    deviceDto.setMasterKey(new byte[16]);
    deviceDto.setAuthenticationKey(new byte[16]);
    deviceDto.setGlobalEncryptionUnicastKey(new byte[16]);
    deviceDto.setMbusDefaultKey(new byte[0]);
    final DlmsDevice dlmsDevice = new DlmsDevice();
    when(this.installationMapper.map(deviceDto, DlmsDevice.class)).thenReturn(dlmsDevice);
    when(this.dlmsDeviceRepository.save(dlmsDevice)).thenReturn(dlmsDevice);
    when(this.rsaEncrypter.decrypt(any())).thenReturn(new byte[16]);
    // WHEN
    this.testService.addMeter(this.messageMetadata, deviceDto);
    // THEN
    verify(this.secretManagementService, times(1))
        .storeNewKeys(eq(this.messageMetadata), eq(this.deviceIdentification), any());
    verify(this.secretManagementService, times(1))
        .activateNewKeys(eq(this.messageMetadata), eq(this.deviceIdentification), any());
  }

  @Test
  void addGMeter() throws FunctionalException {
    final SmartMeteringDeviceDto deviceDto = new SmartMeteringDeviceDto();
    deviceDto.setDeviceIdentification(this.deviceIdentification);
    deviceDto.setMasterKey(new byte[0]);
    deviceDto.setAuthenticationKey(new byte[0]);
    deviceDto.setGlobalEncryptionUnicastKey(new byte[0]);
    deviceDto.setMbusDefaultKey(new byte[16]);
    deviceDto.setMbusUserKey(new byte[16]);
    deviceDto.setMbusFirmwareUpdateAuthenticationKey(new byte[16]);
    deviceDto.setMbusP0Key(new byte[16]);

    final DlmsDevice dlmsDevice = new DlmsDevice();
    final ArgumentCaptor<Map<SecurityKeyType, byte[]>> keysByTypeCaptor =
        ArgumentCaptor.forClass(Map.class);
    final ArgumentCaptor<List<SecurityKeyType>> keyTypesCaptor =
        ArgumentCaptor.forClass(List.class);

    when(this.installationMapper.map(deviceDto, DlmsDevice.class)).thenReturn(dlmsDevice);
    when(this.dlmsDeviceRepository.save(dlmsDevice)).thenReturn(dlmsDevice);
    when(this.rsaEncrypter.decrypt(any())).thenReturn(new byte[16]);

    this.testService.addMeter(this.messageMetadata, deviceDto);

    verify(this.secretManagementService, times(1))
        .storeNewKeys(
            eq(this.messageMetadata), eq(this.deviceIdentification), keysByTypeCaptor.capture());
    final Map<SecurityKeyType, byte[]> capturedKeyTypesToStore = keysByTypeCaptor.getValue();

    assertThat(capturedKeyTypesToStore)
        .hasSize(4)
        .containsKey(SecurityKeyType.G_METER_MASTER)
        .containsKey(SecurityKeyType.G_METER_OPTICAL_PORT_KEY)
        .containsKey(SecurityKeyType.G_METER_ENCRYPTION)
        .containsKey(SecurityKeyType.G_METER_FIRMWARE_UPDATE_AUTHENTICATION);

    verify(this.secretManagementService, times(1))
        .activateNewKeys(
            eq(this.messageMetadata), eq(this.deviceIdentification), keyTypesCaptor.capture());
    final List<SecurityKeyType> capturedKeyTypesToActivate = keyTypesCaptor.getValue();

    assertThat(capturedKeyTypesToActivate)
        .hasSize(4)
        .contains(SecurityKeyType.G_METER_MASTER)
        .contains(SecurityKeyType.G_METER_OPTICAL_PORT_KEY)
        .contains(SecurityKeyType.G_METER_ENCRYPTION)
        .contains(SecurityKeyType.G_METER_FIRMWARE_UPDATE_AUTHENTICATION);
  }

  @Test
  void addMeterNoKeys() {
    // GIVEN
    final SmartMeteringDeviceDto deviceDto = new SmartMeteringDeviceDto();
    deviceDto.setDeviceIdentification(this.deviceIdentification);
    // WHEN
    Assertions.assertThatExceptionOfType(FunctionalException.class)
        .isThrownBy(() -> this.testService.addMeter(this.messageMetadata, deviceDto));
  }

  @Test
  void addMeterRedundantKeys() {
    // GIVEN
    final SmartMeteringDeviceDto deviceDto = new SmartMeteringDeviceDto();
    deviceDto.setDeviceIdentification(this.deviceIdentification);
    deviceDto.setMasterKey(new byte[16]);
    deviceDto.setAuthenticationKey(new byte[16]);
    deviceDto.setGlobalEncryptionUnicastKey(new byte[16]);
    deviceDto.setMbusDefaultKey(new byte[16]);
    // WHEN
    Assertions.assertThatExceptionOfType(FunctionalException.class)
        .isThrownBy(() -> this.testService.addMeter(this.messageMetadata, deviceDto));
  }

  @Test
  void addMeterNoDeviceIdentification() {
    // GIVEN
    final SmartMeteringDeviceDto deviceDto = new SmartMeteringDeviceDto();
    deviceDto.setMbusDefaultKey(new byte[16]);
    // WHEN
    Assertions.assertThatExceptionOfType(FunctionalException.class)
        .isThrownBy(() -> this.testService.addMeter(this.messageMetadata, deviceDto));
  }
}
