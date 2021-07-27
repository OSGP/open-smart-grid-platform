/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetKeysRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetKeysResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.KeyDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SecretTypeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.security.RsaEncryptionService;

@ExtendWith(MockitoExtension.class)
class GetKeysServiceTest {

  private static final String DEVICE_ID = "deviceId";
  private static final DlmsDevice DEVICE = new DlmsDevice(DEVICE_ID);

  @InjectMocks private GetKeysService getKeysService;
  @Mock private SecretManagementService secretManagementService;
  @Mock private RsaEncryptionService rsaEncrypter;

  private static final GetKeysRequestDto REQUEST =
      new GetKeysRequestDto(
          Arrays.asList(
              SecretTypeDto.E_METER_MASTER_KEY, SecretTypeDto.E_METER_AUTHENTICATION_KEY));

  private static final byte[] KEY_1_UNENCRYPTED = new byte[] {1, 2, 3};
  private static final byte[] KEY_2_UNENCRYPTED = new byte[] {4, 5, 6};

  private static final byte[] KEY_1_ENCRYPTED = new byte[] {99, 88, 77};
  private static final byte[] KEY_2_ENCRYPTED = new byte[] {66, 55, 44};

  private static MessageMetadata messageMetadata;

  @BeforeAll
  public static void init() {
    messageMetadata =
        MessageMetadata.newMessageMetadataBuilder().withCorrelationUid("123456").build();
  }

  @Test
  void getKeys() {
    final Map<SecurityKeyType, byte[]> keys = new EnumMap<>(SecurityKeyType.class);

    keys.put(SecurityKeyType.E_METER_MASTER, KEY_1_UNENCRYPTED);
    keys.put(SecurityKeyType.E_METER_AUTHENTICATION, KEY_2_UNENCRYPTED);
    when(this.secretManagementService.getKeys(
            messageMetadata,
            DEVICE_ID,
            Arrays.asList(SecurityKeyType.E_METER_MASTER, SecurityKeyType.E_METER_AUTHENTICATION)))
        .thenReturn(keys);
    when(this.rsaEncrypter.encrypt(KEY_1_UNENCRYPTED)).thenReturn(KEY_1_ENCRYPTED);
    when(this.rsaEncrypter.encrypt(KEY_2_UNENCRYPTED)).thenReturn(KEY_2_ENCRYPTED);
    final GetKeysResponseDto response =
        this.getKeysService.getKeys(DEVICE, REQUEST, messageMetadata);

    final GetKeysResponseDto expectedResponse =
        new GetKeysResponseDto(
            Arrays.asList(
                new KeyDto(SecretTypeDto.E_METER_MASTER_KEY, KEY_1_ENCRYPTED),
                new KeyDto(SecretTypeDto.E_METER_AUTHENTICATION_KEY, KEY_2_ENCRYPTED)));
    assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);
  }

  @Test
  void getKeysWithoutKeyTypes() {
    final GetKeysRequestDto request = new GetKeysRequestDto(Collections.emptyList());
    final GetKeysResponseDto response =
        this.getKeysService.getKeys(DEVICE, request, messageMetadata);

    assertThat(response).isNotNull();
    assertThat(response.getKeys()).isEmpty();
  }

  @Test
  void getKeysWhenKeyNotFound() {
    final Map<SecurityKeyType, byte[]> keys = new EnumMap<>(SecurityKeyType.class);
    keys.put(SecurityKeyType.E_METER_MASTER, KEY_1_UNENCRYPTED);
    keys.put(SecurityKeyType.E_METER_AUTHENTICATION, null);
    when(this.secretManagementService.getKeys(
            messageMetadata,
            DEVICE_ID,
            Arrays.asList(SecurityKeyType.E_METER_MASTER, SecurityKeyType.E_METER_AUTHENTICATION)))
        .thenReturn(keys);
    when(this.rsaEncrypter.encrypt(KEY_1_UNENCRYPTED)).thenReturn(KEY_1_ENCRYPTED);

    final GetKeysResponseDto response =
        this.getKeysService.getKeys(DEVICE, REQUEST, messageMetadata);

    final GetKeysResponseDto expectedResponse =
        new GetKeysResponseDto(
            Arrays.asList(
                new KeyDto(SecretTypeDto.E_METER_MASTER_KEY, KEY_1_ENCRYPTED),
                new KeyDto(SecretTypeDto.E_METER_AUTHENTICATION_KEY, null)));
    assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);
  }
}
