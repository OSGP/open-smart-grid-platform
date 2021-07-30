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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetKeysRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetKeysResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.KeyDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SecretTypeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.security.RsaEncryptionService;
import org.opensmartgridplatform.ws.schema.core.secret.management.SecretType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetKeysService {

  private final SecretManagementService secretManagementService;
  private final RsaEncryptionService keyEncrypter;

  @Autowired
  public GetKeysService(
      final SecretManagementService secretManagementService,
      final RsaEncryptionService keyEncrypter) {

    this.secretManagementService = secretManagementService;
    this.keyEncrypter = keyEncrypter;
  }

  public GetKeysResponseDto getKeys(
      final DlmsDevice device,
      final GetKeysRequestDto getKeysRequestDto,
      final MessageMetadata messageMetadata) {

    final List<SecurityKeyType> securityKeyTypes =
        getKeysRequestDto.getSecretTypes().stream()
            .map(this::convertToSecurityKeyType)
            .collect(Collectors.toList());

    final Map<SecurityKeyType, byte[]> unencryptedKeys =
        this.secretManagementService.getKeys(
            messageMetadata, device.getDeviceIdentification(), securityKeyTypes);

    final List<KeyDto> encryptedKeys = this.convertToKeyDtosWithEncryptedKeys(unencryptedKeys);

    return new GetKeysResponseDto(encryptedKeys);
  }

  private List<KeyDto> convertToKeyDtosWithEncryptedKeys(
      final Map<SecurityKeyType, byte[]> unencryptedKeys) {
    return unencryptedKeys.entrySet().stream()
        .map(entry -> this.convertToKeyDtoWithEncryptedKey(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
  }

  private KeyDto convertToKeyDtoWithEncryptedKey(
      final SecurityKeyType securityKeyType, final byte[] unencryptedKey) {

    if (unencryptedKey != null) {
      final byte[] encryptedKey = this.keyEncrypter.encrypt(unencryptedKey);
      return new KeyDto(this.convertToSecretTypeDto(securityKeyType), encryptedKey);
    } else {
      return new KeyDto(this.convertToSecretTypeDto(securityKeyType), null);
    }
  }

  private SecurityKeyType convertToSecurityKeyType(final SecretTypeDto secretTypeDto) {
    return SecurityKeyType.fromSecretType(SecretType.fromValue(secretTypeDto.name()));
  }

  private SecretTypeDto convertToSecretTypeDto(final SecurityKeyType securityKeyType) {
    return SecretTypeDto.valueOf(securityKeyType.toSecretType().name());
  }
}
