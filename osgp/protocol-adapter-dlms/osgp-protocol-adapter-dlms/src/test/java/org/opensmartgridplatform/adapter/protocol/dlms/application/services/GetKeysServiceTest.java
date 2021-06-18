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
import org.opensmartgridplatform.shared.security.RsaEncrypter;

@ExtendWith(MockitoExtension.class)
class GetKeysServiceTest {

  private static final String DEVICE_ID = "deviceId";
  private static final DlmsDevice DEVICE = new DlmsDevice(DEVICE_ID);

  @InjectMocks private GetKeysService getKeysService;
  @Mock private SecretManagementService secretManagementService;
  @Mock private RsaEncrypter rsaEncrypter;

  private static final GetKeysRequestDto REQUEST =
      new GetKeysRequestDto(
          Arrays.asList(
              SecretTypeDto.E_METER_MASTER_KEY, SecretTypeDto.E_METER_AUTHENTICATION_KEY));

  private static final byte[] KEY_1_UNENCRYPTED = new byte[] {1, 2, 3};
  private static final byte[] KEY_2_UNENCRYPTED = new byte[] {4, 5, 6};

  private static final byte[] KEY_1_ENCRYPTED = new byte[] {99, 88, 77};
  private static final byte[] KEY_2_ENCRYPTED = new byte[] {66, 55, 44};

  @Test
  void getKeys() {
    when(this.secretManagementService.getKey(DEVICE_ID, SecurityKeyType.E_METER_MASTER))
        .thenReturn(KEY_1_UNENCRYPTED);
    when(this.secretManagementService.getKey(DEVICE_ID, SecurityKeyType.E_METER_AUTHENTICATION))
        .thenReturn(KEY_2_UNENCRYPTED);
    when(this.rsaEncrypter.encrypt(KEY_1_UNENCRYPTED)).thenReturn(KEY_1_ENCRYPTED);
    when(this.rsaEncrypter.encrypt(KEY_2_UNENCRYPTED)).thenReturn(KEY_2_ENCRYPTED);
    final GetKeysResponseDto response = this.getKeysService.getKeys(DEVICE, REQUEST);

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

    final GetKeysResponseDto response = this.getKeysService.getKeys(DEVICE, request);

    assertThat(response).isNotNull();
    assertThat(response.getKeys()).isEmpty();
  }

  @Test
  void getKeysWhenKeyNotFound() {
    when(this.secretManagementService.getKey(DEVICE_ID, SecurityKeyType.E_METER_MASTER))
        .thenReturn(KEY_1_UNENCRYPTED);
    when(this.secretManagementService.getKey(DEVICE_ID, SecurityKeyType.E_METER_AUTHENTICATION))
        .thenReturn(null);
    when(this.rsaEncrypter.encrypt(KEY_1_UNENCRYPTED)).thenReturn(KEY_1_ENCRYPTED);

    final GetKeysResponseDto response = this.getKeysService.getKeys(DEVICE, REQUEST);

    final GetKeysResponseDto expectedResponse =
        new GetKeysResponseDto(
            Arrays.asList(
                new KeyDto(SecretTypeDto.E_METER_MASTER_KEY, KEY_1_ENCRYPTED),
                new KeyDto(SecretTypeDto.E_METER_AUTHENTICATION_KEY, null)));
    assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);
  }
}
