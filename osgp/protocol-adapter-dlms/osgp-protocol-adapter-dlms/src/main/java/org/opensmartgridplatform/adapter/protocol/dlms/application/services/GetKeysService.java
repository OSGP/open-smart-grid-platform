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

import java.util.ArrayList;
import java.util.List;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetKeysRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetKeysResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.KeyDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SecretTypeDto;
import org.opensmartgridplatform.shared.security.RsaEncrypter;
import org.opensmartgridplatform.ws.schema.core.secret.management.SecretType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class GetKeysService {

  @Autowired private SecretManagementService secretManagementService;

  @Autowired private RsaEncrypter rsaEncrypter;

  public GetKeysResponseDto getKeys(
      final DlmsDevice device, final GetKeysRequestDto getKeysRequestDto) {

    final List<KeyDto> keys =
        this.getEncryptedKeys(device.getDeviceIdentification(), getKeysRequestDto.getsecretTypes());

    return new GetKeysResponseDto(keys);
  }

  private List<KeyDto> getEncryptedKeys(
      final String deviceIdentification, final List<SecretTypeDto> secretTypes) {

    final List<KeyDto> encryptedKeys = new ArrayList<>();

    for (final SecretTypeDto keyType : secretTypes) {
      final KeyDto encryptedKey = this.getEncryptedKey(deviceIdentification, keyType);
      encryptedKeys.add(encryptedKey);
    }

    return encryptedKeys;
  }

  private KeyDto getEncryptedKey(
      final String deviceIdentification, final SecretTypeDto secretTypeDto) {
    final SecurityKeyType securityKeyType = this.convertToSecurityKeyType(secretTypeDto);

    final byte[] unencryptedKey =
        this.secretManagementService.getKey(deviceIdentification, securityKeyType);

    if (unencryptedKey == null) {
      // return null to indicate the key is not found
      return new KeyDto(secretTypeDto, null);
    }

    final byte[] encryptedKey = this.rsaEncrypter.encrypt(unencryptedKey);

    return new KeyDto(secretTypeDto, encryptedKey);
  }

  private SecurityKeyType convertToSecurityKeyType(final SecretTypeDto secretTypeDto) {
    return SecurityKeyType.fromSecretType(SecretType.fromValue(secretTypeDto.name()));
  }
}
