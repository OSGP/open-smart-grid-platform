/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.G_METER_ENCRYPTION;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.G_METER_MASTER;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SecretManagementService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SecretTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetEncryptionKeyExchangeOnGMeterRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class SetKeyOnGMeterCommandExecutorTest {

  private final String DEVICE_IDENTIFICATION_E = "E-meter DeviceId";
  private final String DEVICE_IDENTIFICATION_G = "G-meter DeviceId";
  private final DlmsDevice DEVICE_E_DSMR4 =
      this.createEMeter(Protocol.DSMR_4_2_2, this.DEVICE_IDENTIFICATION_E);
  private final DlmsDevice DEVICE_G_DSMR4 =
      this.createGMeter(Protocol.DSMR_4_2_2, this.DEVICE_IDENTIFICATION_G, "12345678", "ABC");
  private final DlmsDevice DEVICE_E_SMR5 =
      this.createEMeter(Protocol.SMR_5_0_0, this.DEVICE_IDENTIFICATION_E);
  private final DlmsDevice DEVICE_G_SMR5 =
      this.createGMeter(Protocol.SMR_5_0_0, this.DEVICE_IDENTIFICATION_G, "12345678", "ABC");
  private final byte[] MASTER_KEY =
      new byte[] {11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
  private final byte[] NEW_KEY = new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
  private final int KEY_SIZE = 16;
  private final int KEY_DATA_SIZE_SMR5 = 26; // KEY_SIZE + 10 bytes (keyId, keySize and mac)
  private final int CLASS_ID = 72; // M-Bus client setup
  private final int METHOD_ID_DATA_SEND = 6;
  private final int METHOD_ID_SET_ENCRYPTION_KEY = 7;
  private final int METHOD_ID_TRANSFER_KEY = 8;

  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Mock private DlmsConnection dlmsConnection;

  @Mock private MessageMetadata messageMetadata;

  @Mock private SecretManagementService secretManagementService;

  @Mock private DlmsDeviceRepository dlmsDeviceRepository;

  @InjectMocks private SetKeyOnGMeterCommandExecutor executor;

  @Captor ArgumentCaptor<MethodParameter> methodParameterArgumentCaptor;

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 4})
  void testSetEncryptionKeyDsmr4(final int channel) throws ProtocolAdapterException, IOException {
    // SETUP
    final MethodResult methodResult = mock(MethodResult.class);
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.action(any(MethodParameter.class))).thenReturn(methodResult);
    when(methodResult.getResultCode()).thenReturn(MethodResultCode.SUCCESS);
    when(this.dlmsDeviceRepository.findByDeviceIdentification(this.DEVICE_IDENTIFICATION_G))
        .thenReturn(this.DEVICE_G_DSMR4);
    when(this.secretManagementService.generate128BitsKeyAndStoreAsNewKey(
            this.messageMetadata, this.DEVICE_IDENTIFICATION_G, G_METER_ENCRYPTION))
        .thenReturn(this.NEW_KEY);
    when(this.secretManagementService.getKey(
            this.messageMetadata, this.DEVICE_IDENTIFICATION_G, G_METER_MASTER))
        .thenReturn(this.MASTER_KEY);

    final SetEncryptionKeyExchangeOnGMeterRequestDto requestDto =
        new SetEncryptionKeyExchangeOnGMeterRequestDto(
            this.DEVICE_IDENTIFICATION_G, channel, SecretTypeDto.G_METER_ENCRYPTION_KEY, false);

    // CALL
    final MethodResultCode resultCode =
        this.executor.execute(this.conn, this.DEVICE_E_DSMR4, requestDto, this.messageMetadata);

    // VERIFY
    assertThat(resultCode).isEqualTo(MethodResultCode.SUCCESS);
    verify(this.dlmsConnection, times(2)).action(this.methodParameterArgumentCaptor.capture());
    final List<MethodParameter> methodParameters =
        this.methodParameterArgumentCaptor.getAllValues();

    // Verify parameters in call to perform transfer_key (note: new key is encrypted)
    this.verifyMethodParameter(
        methodParameters.get(0), channel, this.METHOD_ID_TRANSFER_KEY, this.KEY_SIZE, false);

    // Verify parameters in call to perform set_encryption_key (note: new key is not encrypted)
    this.verifyMethodParameter(
        methodParameters.get(1), channel, this.METHOD_ID_SET_ENCRYPTION_KEY, this.KEY_SIZE, true);
  }

  @ParameterizedTest
  //  @ValueSource(ints = {1, 2, 3, 4})
  @CsvSource({
    "1,G_METER_ENCRYPTION_KEY,false",
    "2,G_METER_ENCRYPTION_KEY,false",
    "3,G_METER_ENCRYPTION_KEY,false",
    "4,G_METER_ENCRYPTION_KEY,false",
    "1,G_METER_FIRMWARE_UPDATE_AUTHENTICATION_KEY,false",
    "2,G_METER_OPTICAL_PORT_KEY,false",
    "3,G_METER_OPTICAL_PORT_KEY,true"
  })
  void testSetEncryptionKeySmr5(
      final int channel, final String secretType, final boolean closeOpticalPort)
      throws ProtocolAdapterException, IOException {
    // SETUP
    final SecretTypeDto secretTypeDto = SecretTypeDto.valueOf(secretType);
    final MethodResult methodResult = mock(MethodResult.class);
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.action(any(MethodParameter.class))).thenReturn(methodResult);
    when(methodResult.getResultCode()).thenReturn(MethodResultCode.SUCCESS);
    when(this.dlmsDeviceRepository.findByDeviceIdentification(this.DEVICE_IDENTIFICATION_G))
        .thenReturn(this.DEVICE_G_SMR5);
    when(this.secretManagementService.getKey(
            this.messageMetadata, this.DEVICE_IDENTIFICATION_G, G_METER_MASTER))
        .thenReturn(this.MASTER_KEY);
    if (!secretTypeDto.equals(SecretTypeDto.G_METER_OPTICAL_PORT_KEY) || !closeOpticalPort) {
      // When the optical port has to be closed, no new key needs to be generated.
      when(this.secretManagementService.generate128BitsKeyAndStoreAsNewKey(
              eq(this.messageMetadata), eq(this.DEVICE_IDENTIFICATION_G), any()))
          .thenReturn(this.NEW_KEY);
    }

    final SetEncryptionKeyExchangeOnGMeterRequestDto requestDto =
        new SetEncryptionKeyExchangeOnGMeterRequestDto(
            this.DEVICE_IDENTIFICATION_G,
            channel,
            SecretTypeDto.valueOf(secretType),
            closeOpticalPort);

    // CALL
    final MethodResultCode resultCode =
        this.executor.execute(this.conn, this.DEVICE_E_SMR5, requestDto, this.messageMetadata);

    // VERIFY
    assertThat(resultCode).isEqualTo(MethodResultCode.SUCCESS);

    if (secretTypeDto.equals(SecretTypeDto.G_METER_ENCRYPTION_KEY)) {

      verify(this.dlmsConnection, times(2)).action(this.methodParameterArgumentCaptor.capture());
      final List<MethodParameter> methodParameters =
          this.methodParameterArgumentCaptor.getAllValues();

      // Verify parameters in call to perform transfer_key (note: new key is encrypted)
      this.verifyMethodParameter(
          methodParameters.get(0),
          channel,
          this.METHOD_ID_TRANSFER_KEY,
          this.KEY_DATA_SIZE_SMR5,
          false);

      // Verify parameters in call to perform set_encryption_key (note: new key is not encrypted)
      this.verifyMethodParameter(
          methodParameters.get(1), channel, this.METHOD_ID_SET_ENCRYPTION_KEY, this.KEY_SIZE, true);

    } else {

      verify(this.dlmsConnection, times(1)).action(this.methodParameterArgumentCaptor.capture());
      final List<MethodParameter> methodParameters =
          this.methodParameterArgumentCaptor.getAllValues();

      // Verify parameters in call to perform data_send (note: new key is encrypted)
      this.verifyMethodParameter(
          methodParameters.get(0),
          channel,
          this.METHOD_ID_DATA_SEND,
          this.KEY_DATA_SIZE_SMR5,
          false);
    }
  }

  @Test
  void testSetEncryptionKeyWithUnknownDevice() {
    // SETUP
    when(this.dlmsDeviceRepository.findByDeviceIdentification(this.DEVICE_IDENTIFICATION_G))
        .thenReturn(null);

    final SetEncryptionKeyExchangeOnGMeterRequestDto requestDto =
        new SetEncryptionKeyExchangeOnGMeterRequestDto(
            this.DEVICE_IDENTIFICATION_G, 1, SecretTypeDto.G_METER_ENCRYPTION_KEY, false);

    // CALL
    assertThrows(
        ProtocolAdapterException.class,
        () ->
            this.executor.execute(this.conn, this.DEVICE_E_SMR5, requestDto, this.messageMetadata));
  }

  @Test
  void testSetEncryptionKeyWithInvalidKeyType() {
    // SETUP
    when(this.dlmsDeviceRepository.findByDeviceIdentification(this.DEVICE_IDENTIFICATION_G))
        .thenReturn(this.DEVICE_G_SMR5);

    final SetEncryptionKeyExchangeOnGMeterRequestDto requestDto =
        new SetEncryptionKeyExchangeOnGMeterRequestDto(
            this.DEVICE_IDENTIFICATION_G, 1, SecretTypeDto.E_METER_MASTER_KEY, false);

    // CALL
    assertThrows(
        ProtocolAdapterException.class,
        () ->
            this.executor.execute(this.conn, this.DEVICE_E_SMR5, requestDto, this.messageMetadata));
  }

  @Test
  void testSetEncryptionKeyWithInvalidCombinationOfProtocolAndKeyType() {
    // SETUP
    when(this.dlmsDeviceRepository.findByDeviceIdentification(this.DEVICE_IDENTIFICATION_G))
        .thenReturn(this.DEVICE_G_DSMR4);

    final SetEncryptionKeyExchangeOnGMeterRequestDto requestDto =
        new SetEncryptionKeyExchangeOnGMeterRequestDto(
            this.DEVICE_IDENTIFICATION_G, 1, SecretTypeDto.G_METER_OPTICAL_PORT_KEY, false);

    // CALL
    assertThrows(
        ProtocolAdapterException.class,
        () ->
            this.executor.execute(
                this.conn, this.DEVICE_E_DSMR4, requestDto, this.messageMetadata));
  }

  private DlmsDevice createEMeter(final Protocol protocol, final String deviceIdentification) {
    final DlmsDevice device = new DlmsDevice();
    device.setProtocol(protocol);
    device.setDeviceIdentification(deviceIdentification);
    return device;
  }

  private DlmsDevice createGMeter(
      final Protocol protocol,
      final String deviceIdentification,
      final String mbusIdentificationNumber,
      final String mbusManufacturer) {
    final DlmsDevice device = new DlmsDevice();
    device.setProtocol(protocol);
    device.setDeviceIdentification(deviceIdentification);
    device.setMbusIdentificationNumber(mbusIdentificationNumber);
    device.setMbusManufacturerIdentification(mbusManufacturer);
    return device;
  }

  private ObisCode obisCodeForChannel(final int channel) {
    return new ObisCode(String.format("0.%d.24.1.0.255", channel));
  }

  private void verifyMethodParameter(
      final MethodParameter methodParameter,
      final int channel,
      final int methodId,
      final int expectedKeyDataSize,
      final boolean shouldBeEqualToNewKey) {
    assertThat(methodParameter.getClassId()).isEqualTo(this.CLASS_ID);
    assertThat(methodParameter.getInstanceId()).isEqualTo(this.obisCodeForChannel(channel));
    assertThat(methodParameter.getId()).isEqualTo(methodId);

    assertThat(((byte[]) ((DataObject) methodParameter.getParameter()).getValue()))
        .hasSize(expectedKeyDataSize);

    if (shouldBeEqualToNewKey) {
      assertThat(methodParameter.getParameter())
          .withFailMessage("The unencrypted key should be equal to the original new key")
          .usingRecursiveComparison()
          .isEqualTo(DataObject.newOctetStringData(this.NEW_KEY));
    } else {
      assertThat(methodParameter.getParameter())
          .withFailMessage("The encrypted key should not be equal to the original new key")
          .usingRecursiveComparison()
          .isNotEqualTo(DataObject.newOctetStringData(this.NEW_KEY));
    }
  }
}
