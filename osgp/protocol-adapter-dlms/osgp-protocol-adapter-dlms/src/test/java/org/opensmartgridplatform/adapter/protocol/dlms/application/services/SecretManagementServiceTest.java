/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.application.wsclient.SecretManagementClient;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.security.RsaEncrypter;
import org.opensmartgridplatform.ws.schema.core.secret.management.ActivateSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.GenerateAndStoreSecretsResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.GetSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.GetSecretsResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.HasNewSecretResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.OsgpResultType;
import org.opensmartgridplatform.ws.schema.core.secret.management.StoreSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.StoreSecretsResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecret;
import org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecrets;

@ExtendWith(MockitoExtension.class)
public class SecretManagementServiceTest {
  @InjectMocks SecretManagementService testService;

  @Mock SecretManagementClient secretManagementClient;
  @Mock RsaEncrypter rsaEncrypter;

  private static final String DEVICE_IDENTIFICATION = "E000123456789";
  private static final SecurityKeyType KEY_TYPE = SecurityKeyType.E_METER_ENCRYPTION;
  private static final byte[] UNENCRYPTED_SECRET = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
  private static final byte[] SOAP_SECRET = {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
  private static final String HEX_SOAP_SECRET = Hex.encodeHexString(SOAP_SECRET);
  private static final TypedSecret TYPED_SECRET = new TypedSecret();

  private static MessageMetadata messageMetadata;

  @BeforeAll
  public static void init() {
    TYPED_SECRET.setType(KEY_TYPE.toSecretType());
    TYPED_SECRET.setSecret(HEX_SOAP_SECRET);
    messageMetadata = MessageMetadata.newBuilder().withCorrelationUid("123456").build();
  }

  @Test
  public void testGetKeys() {
    // SETUP
    final List<SecurityKeyType> keyTypes = Arrays.asList(KEY_TYPE);
    final GetSecretsResponse response = new GetSecretsResponse();
    response.setResult(OsgpResultType.OK);
    response.setTypedSecrets(new TypedSecrets());
    response.getTypedSecrets().getTypedSecret().add(TYPED_SECRET);
    when(this.secretManagementClient.getSecretsRequest(
            same(messageMetadata), any(GetSecretsRequest.class)))
        .thenReturn(response);
    when(this.rsaEncrypter.decrypt(SOAP_SECRET)).thenReturn(UNENCRYPTED_SECRET);
    // EXECUTE
    final Map<SecurityKeyType, byte[]> result =
        this.testService.getKeys(messageMetadata, DEVICE_IDENTIFICATION, keyTypes);
    // ASSERT
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(1);
    assertThat(result.keySet().iterator().next()).isEqualTo(KEY_TYPE);
    assertThat(result.values().iterator().next()).isEqualTo(UNENCRYPTED_SECRET);
  }

  @Test
  public void testStoreNewKeys() {
    final Map<SecurityKeyType, byte[]> keys = new HashMap<>();
    keys.put(KEY_TYPE, UNENCRYPTED_SECRET);
    final StoreSecretsResponse response = new StoreSecretsResponse();
    response.setResult(OsgpResultType.OK);
    when(this.rsaEncrypter.encrypt(UNENCRYPTED_SECRET)).thenReturn(SOAP_SECRET);
    when(this.secretManagementClient.storeSecretsRequest(same(messageMetadata), any()))
        .thenReturn(response);
    // EXECUTE
    this.testService.storeNewKeys(messageMetadata, DEVICE_IDENTIFICATION, keys);
    // ASSERT
    verify(this.secretManagementClient, times(1))
        .storeSecretsRequest(same(messageMetadata), any(StoreSecretsRequest.class));
  }

  @Test
  public void testActivateKeys() throws ProtocolAdapterException {
    final List<SecurityKeyType> keyTypes = Arrays.asList(KEY_TYPE);
    final ArgumentCaptor<ActivateSecretsRequest> activateSecretsCaptor =
        ArgumentCaptor.forClass(ActivateSecretsRequest.class);
    // EXECUTE
    this.testService.activateNewKeys(messageMetadata, DEVICE_IDENTIFICATION, keyTypes);
    // ASSERT
    verify(this.secretManagementClient)
        .activateSecretsRequest(same(messageMetadata), activateSecretsCaptor.capture());
    final ActivateSecretsRequest capturedArgument = activateSecretsCaptor.getValue();
    assertThat(capturedArgument.getDeviceId()).isEqualTo(DEVICE_IDENTIFICATION);
    assertThat(capturedArgument.getSecretTypes().getSecretType().get(0))
        .isEqualTo(KEY_TYPE.toSecretType());
  }

  @Test
  public void testGenerateAndStoreKeys() {
    final List<SecurityKeyType> keyTypes = Arrays.asList(KEY_TYPE);
    final GenerateAndStoreSecretsResponse response = new GenerateAndStoreSecretsResponse();
    response.setResult(OsgpResultType.OK);
    response.setTypedSecrets(new TypedSecrets());
    response.getTypedSecrets().getTypedSecret().add(TYPED_SECRET);
    when(this.secretManagementClient.generateAndStoreSecrets(same(messageMetadata), any()))
        .thenReturn(response);
    when(this.rsaEncrypter.decrypt(SOAP_SECRET)).thenReturn(UNENCRYPTED_SECRET);
    // EXECUTE
    final Map<SecurityKeyType, byte[]> keys =
        this.testService.generate128BitsKeysAndStoreAsNewKeys(
            messageMetadata, DEVICE_IDENTIFICATION, keyTypes);
    // ASSERT
    assertThat(keys.get(KEY_TYPE)).isEqualTo(UNENCRYPTED_SECRET);
  }

  @Test
  public void testHasNewKey() {
    final HasNewSecretResponse response = new HasNewSecretResponse();
    response.setHasNewSecret(true);
    when(this.secretManagementClient.hasNewSecretRequest(same(messageMetadata), any()))
        .thenReturn(response);
    // EXECUTE
    final boolean result =
        this.testService.hasNewSecretOfType(messageMetadata, DEVICE_IDENTIFICATION, KEY_TYPE);
    // ASSERT
    assertThat(result).isTrue();
  }
}
