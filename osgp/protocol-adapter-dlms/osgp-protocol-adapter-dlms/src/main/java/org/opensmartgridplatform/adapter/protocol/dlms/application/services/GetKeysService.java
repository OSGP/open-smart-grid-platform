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
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetKeysRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetKeysResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.KeyDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SecretTypeDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.security.RsaEncrypter;
import org.opensmartgridplatform.ws.schema.core.secret.management.SecretType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class GetKeysService {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetKeysService.class);

  @Autowired private SecretManagementService secretManagementService;

  @Autowired private EncryptionHelperService encryptionHelperService;

  @Autowired private RsaEncrypter rsaEncrypter;

  public GetKeysResponseDto getKeys(
      final DlmsDevice device, final GetKeysRequestDto getKeysRequestDto) throws OsgpException {

    final List<KeyDto> keys =
        this.getEncryptedKeys(device.getDeviceIdentification(), getKeysRequestDto.getsecretTypes());

    return new GetKeysResponseDto(keys);
    //
    //    return new GetKeysResponseDto(
    //        Arrays.asList(
    //            new KeyDto(SecretTypeDto.E_METER_AUTHENTICATION_KEY, new byte[] {10, 11, 12}),
    //            new KeyDto(SecretTypeDto.G_METER_ENCRYPTION_KEY, new byte[] {20, 21, 22})));
  }

  private List<KeyDto> getEncryptedKeys(
      final String deviceIdentification, final List<SecretTypeDto> secretTypes)
      throws OsgpException {

    final List<KeyDto> encryptedKeys = new ArrayList<>();

    for (final SecretTypeDto keyType : secretTypes) {
      final KeyDto encryptedKey = this.getEncryptedKey(deviceIdentification, keyType);
      encryptedKeys.add(encryptedKey);
    }

    return encryptedKeys;
  }

  private KeyDto getEncryptedKey(
      final String deviceIdentification, final SecretTypeDto secretTypeDto) throws OsgpException {
    final SecurityKeyType securityKeyType = this.convertToSecurityKeyType(secretTypeDto);

    final byte[] unencryptedKey =
        this.secretManagementService.getKey(deviceIdentification, securityKeyType);

    //    final byte[] encryptedKey = this.encryptionHelperService.rsaEncrypt(unencryptedKey);

    if (unencryptedKey == null) {
      // return null to indicate the key is not found
      return new KeyDto(secretTypeDto, null);
    }

    final byte[] encryptedKey = this.rsaEncrypter.encrypt(unencryptedKey);

    return new KeyDto(secretTypeDto, encryptedKey);
  }

  private SecurityKeyType convertToSecurityKeyType(final SecretTypeDto secretTypeDto)
      throws ProtocolAdapterException {

    return SecurityKeyType.fromSecretType(SecretType.fromValue(secretTypeDto.name()));
  }
}
