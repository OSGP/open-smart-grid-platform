/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.tomcat.util.buf.HexUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretStatus;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretType;
import org.opensmartgridplatform.secretmanagement.application.domain.TypedSecret;
import org.opensmartgridplatform.secretmanagement.application.repository.DbEncryptedSecretRepository;
import org.opensmartgridplatform.secretmanagement.application.repository.DbEncryptionKeyRepository;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.security.EncryptedSecret;
import org.opensmartgridplatform.shared.security.EncryptionDelegate;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;
import org.opensmartgridplatform.shared.security.RsaEncrypter;

@ExtendWith(MockitoExtension.class)
public class SecretManagementServiceTest {

  private static final String SOME_DEVICE = "SOME_DEVICE";
  private static final EncryptionProviderType ENCRYPTION_PROVIDER_TYPE = EncryptionProviderType.HSM;

  private SecretManagementService service;

  @Mock private EncryptionDelegate encryptionDelegate;
  @Mock private DbEncryptedSecretRepository secretRepository;
  @Mock private DbEncryptionKeyRepository keyRepository;
  @Mock private RsaEncrypter rsaEncrypter;

  @BeforeEach
  public void setHsmEncryption() {
    this.service =
        new SecretManagementService(
            this.encryptionDelegate,
            ENCRYPTION_PROVIDER_TYPE,
            this.secretRepository,
            this.keyRepository,
            this.rsaEncrypter);
  }

  @Test
  public void retrieveSecrets() throws Exception {
    // GIVEN
    final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
    keyReference.setEncryptionProviderType(ENCRYPTION_PROVIDER_TYPE);
    keyReference.setReference("1");
    final DbEncryptedSecret secret = new DbEncryptedSecret();
    secret.setSecretType(SecretType.E_METER_MASTER_KEY);
    secret.setEncryptionKeyReference(keyReference);
    secret.setEncodedSecret("ABCDEF01234567890123456789ABCDEF");
    final List<DbEncryptedSecret> secretList = Arrays.asList(secret);
    final byte[] decryptedSecret = "secret0000000001".getBytes();
    final byte[] rsaSecret = "1000000000terces".getBytes();

    // WHEN
    when(this.secretRepository.findSecrets(
            SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.ACTIVE))
        .thenReturn(secretList);
    when(this.encryptionDelegate.decrypt(any(), any())).thenReturn(decryptedSecret);
    when(this.rsaEncrypter.encrypt(any())).thenReturn(rsaSecret);
    final List<TypedSecret> typedSecrets =
        this.service.retrieveSecrets(SOME_DEVICE, Arrays.asList(SecretType.E_METER_MASTER_KEY));

    // THEN
    assertThat(typedSecrets).isNotNull();
    assertThat(typedSecrets.size()).isEqualTo(1);
    final TypedSecret typedSecret = typedSecrets.get(0);
    assertThat(typedSecret.getSecret()).isEqualTo(rsaSecret);
    assertThat(typedSecret.getSecretType()).isEqualTo(SecretType.E_METER_MASTER_KEY);
  }

  @Test
  public void retrieveSecretsDecryptionError() throws EncrypterException {
    // GIVEN
    final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
    keyReference.setEncryptionProviderType(ENCRYPTION_PROVIDER_TYPE);
    keyReference.setReference("1");
    final DbEncryptedSecret secret = new DbEncryptedSecret();
    secret.setSecretType(SecretType.E_METER_MASTER_KEY);
    secret.setEncryptionKeyReference(keyReference);
    secret.setEncodedSecret("0123456789ABCDEF0123456789ABCDEF");
    final List<DbEncryptedSecret> secretPage = Arrays.asList(secret);

    // WHEN
    when(this.secretRepository.findSecrets(
            SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.ACTIVE))
        .thenReturn(secretPage);
    when(this.encryptionDelegate.decrypt(any(), any()))
        .thenThrow(new EncrypterException("Decryption error"));

    // THEN
    assertThatIllegalStateException()
        .isThrownBy(
            () ->
                this.service.retrieveSecrets(
                    "SOME_DEVICE", Arrays.asList(SecretType.E_METER_MASTER_KEY)));
  }

  @Test
  public void retrieveSecretsSecretWithoutKey() {
    final DbEncryptedSecret secret = new DbEncryptedSecret();
    final List<DbEncryptedSecret> secretList = Arrays.asList(secret);
    when(this.secretRepository.findSecrets(
            SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.ACTIVE))
        .thenReturn(secretList);
    assertThatIllegalStateException()
        .isThrownBy(
            () ->
                this.service.retrieveSecrets(
                    "SOME_DEVICE", Arrays.asList(SecretType.E_METER_MASTER_KEY)));
  }

  @Test
  public void retrieveSecretsNoSecrets() {
    final List<SecretType> secretTypes = Arrays.asList(SecretType.E_METER_MASTER_KEY);
    final List<TypedSecret> result = this.service.retrieveSecrets("SOME_DEVICE", secretTypes);
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(secretTypes.size());
    assertThat(result.get(0)).isNotNull();
    assertThat(result.get(0).getSecretType()).isEqualTo(SecretType.E_METER_MASTER_KEY);
    assertThat(result.get(0).getSecret()).isNull();
  }

  @Test
  public void storeSecrets() throws Exception {
    // GIVEN
    final TypedSecret typedSecret =
        new TypedSecret("rsaSecret00000001".getBytes(), SecretType.E_METER_MASTER_KEY);
    final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
    keyReference.setEncryptionProviderType(ENCRYPTION_PROVIDER_TYPE);
    keyReference.setReference("1");
    final EncryptedSecret encryptedSecret =
        new EncryptedSecret(ENCRYPTION_PROVIDER_TYPE, "aesSecret0000001".getBytes());

    // WHEN
    when(this.secretRepository.getSecretCount(
            SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.NEW))
        .thenReturn(0);
    when(this.keyRepository.findByTypeAndValid(any(), any()))
        .thenReturn(Arrays.asList(keyReference));
    when(this.keyRepository.findByTypeAndReference(ENCRYPTION_PROVIDER_TYPE, "1"))
        .thenReturn(keyReference);
    when(this.encryptionDelegate.encrypt(any(), any(), anyString())).thenReturn(encryptedSecret);
    this.service.storeSecrets("SOME_DEVICE", Arrays.asList(typedSecret));
    // THEN
    final ArgumentCaptor<List<DbEncryptedSecret>> secretListArgumentCaptor =
        ArgumentCaptor.forClass(List.class);
    verify(this.secretRepository).saveAll(secretListArgumentCaptor.capture());
    final List<DbEncryptedSecret> savedSecrets = secretListArgumentCaptor.getValue();
    assertThat(savedSecrets).isNotNull();
    assertThat(savedSecrets.size()).isEqualTo(1);
    final DbEncryptedSecret savedSecret = savedSecrets.get(0);
    assertThat(savedSecret.getDeviceIdentification()).isEqualTo("SOME_DEVICE");
    assertThat(savedSecret.getSecretType()).isEqualTo(typedSecret.getSecretType());
    assertThat(savedSecret.getEncodedSecret())
        .isEqualTo(HexUtils.toHexString(encryptedSecret.getSecret()));
    assertThat(savedSecret.getEncryptionKeyReference()).isEqualTo(keyReference);
    assertThat(savedSecret.getCreationTime()).isNotNull();
  }

  @Test
  public void storeSecretsNewKeyAlreadyStored() throws Exception {
    // GIVEN
    final TypedSecret typedSecret = new TypedSecret(new byte[16], SecretType.E_METER_MASTER_KEY);
    final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
    keyReference.setEncryptionProviderType(ENCRYPTION_PROVIDER_TYPE);
    keyReference.setReference("1");
    // WHEN
    when(this.secretRepository.getSecretCount(
            SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.NEW))
        .thenReturn(1);
    // THEN
    assertThatIllegalStateException()
        .isThrownBy(() -> this.service.storeSecrets("SOME_DEVICE", Arrays.asList(typedSecret)));
  }

  @Test
  public void storeSecretsNoKey() {
    // GIVEN
    final TypedSecret typedSecret =
        new TypedSecret("$3cr3t".getBytes(), SecretType.E_METER_MASTER_KEY);
    // WHEN
    when(this.keyRepository.findByTypeAndValid(any(), any())).thenReturn(Arrays.asList());
    // THEN
    assertThatExceptionOfType(NoSuchElementException.class)
        .isThrownBy(() -> this.service.storeSecrets("SOME_DEVICE", Arrays.asList(typedSecret)));
  }

  @Test
  public void storeSecretsMultipleKeys() {
    // GIVEN
    final TypedSecret typedSecret =
        new TypedSecret("$3cr3t".getBytes(), SecretType.E_METER_MASTER_KEY);
    // WHEN
    when(this.keyRepository.findByTypeAndValid(any(), any()))
        .thenReturn(Arrays.asList(new DbEncryptionKeyReference(), new DbEncryptionKeyReference()));
    // THEN
    assertThatIllegalStateException()
        .isThrownBy(() -> this.service.storeSecrets("SOME_DEVICE", Arrays.asList(typedSecret)));
  }

  @Test
  public void storeSecretsEncryptionError() throws EncrypterException {
    // GIVEN
    final TypedSecret typedSecret =
        new TypedSecret("$3cr3t".getBytes(), SecretType.E_METER_MASTER_KEY);
    final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
    keyReference.setEncryptionProviderType(ENCRYPTION_PROVIDER_TYPE);
    keyReference.setReference("keyReferenceString");

    // WHEN
    when(this.keyRepository.findByTypeAndValid(any(), any()))
        .thenReturn(Arrays.asList(keyReference));
    when(this.encryptionDelegate.encrypt(any(), any(), anyString()))
        .thenThrow(new EncrypterException("Encryption error"));
    // THEN
    assertThatIllegalStateException()
        .isThrownBy(() -> this.service.storeSecrets("SOME_DEVICE", Arrays.asList(typedSecret)));
  }

  @Test
  public void storeSecretsExistingSecret() throws Exception {
    // GIVEN
    final TypedSecret typedSecret =
        new TypedSecret("n3w$3cr3t0000001".getBytes(), SecretType.E_METER_MASTER_KEY);
    final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
    keyReference.setEncryptionProviderType(ENCRYPTION_PROVIDER_TYPE);
    keyReference.setReference("1");
    final EncryptedSecret encryptedSecret =
        new EncryptedSecret(ENCRYPTION_PROVIDER_TYPE, "n3w$3cr3t0000001".getBytes());
    final DbEncryptedSecret existingDbSecret = new DbEncryptedSecret();
    existingDbSecret.setCreationTime(new Date());
    existingDbSecret.setSecretType(SecretType.E_METER_MASTER_KEY);
    existingDbSecret.setEncodedSecret("1234567890ABCDEF");
    existingDbSecret.setDeviceIdentification("SOME_DEVICE");
    existingDbSecret.setEncryptionKeyReference(keyReference);

    // WHEN
    when(this.secretRepository.getSecretCount(
            SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.NEW))
        .thenReturn(0);
    when(this.keyRepository.findByTypeAndValid(any(), any()))
        .thenReturn(Arrays.asList(keyReference));
    when(this.keyRepository.findByTypeAndReference(ENCRYPTION_PROVIDER_TYPE, "1"))
        .thenReturn(keyReference);
    when(this.encryptionDelegate.encrypt(any(), any(), anyString()))
        .thenReturn(encryptedSecret); // encrypt new DB secret
    this.service.storeSecrets("SOME_DEVICE", Arrays.asList(typedSecret));
    // THEN
    final ArgumentCaptor<List<DbEncryptedSecret>> secretListArgumentCaptor =
        ArgumentCaptor.forClass(List.class);
    verify(this.secretRepository).saveAll(secretListArgumentCaptor.capture());
    final List<DbEncryptedSecret> savedSecrets = secretListArgumentCaptor.getValue();
    assertThat(savedSecrets).isNotNull();
    assertThat(savedSecrets.size()).isEqualTo(1);
    final DbEncryptedSecret savedSecret = savedSecrets.get(0);
    assertThat(savedSecret).isNotNull();
    assertThat(savedSecret.getDeviceIdentification()).isEqualTo("SOME_DEVICE");
    assertThat(savedSecret.getSecretType()).isEqualTo(typedSecret.getSecretType());
    assertThat(savedSecret.getEncodedSecret())
        .isEqualTo(HexUtils.toHexString(encryptedSecret.getSecret()));
    assertThat(savedSecret.getEncryptionKeyReference()).isEqualTo(keyReference);
    assertThat(savedSecret.getCreationTime()).isNotNull();
  }

  @Test
  public void activateSecretsNoCurrentActiveSecret() {
    // GIVEN
    final DbEncryptedSecret newSecret = new DbEncryptedSecret();
    newSecret.setId(1L);
    newSecret.setSecretStatus(SecretStatus.NEW);
    // WHEN
    when(this.secretRepository.getSecretCount(
            SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.NEW))
        .thenReturn(1);
    when(this.secretRepository.findSecrets(
            SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.ACTIVE))
        .thenReturn(new ArrayList<>());
    when(this.secretRepository.findSecrets(
            SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.NEW))
        .thenReturn(Arrays.asList(newSecret));
    when(this.secretRepository.saveAll(Arrays.asList(newSecret)))
        .thenReturn(Arrays.asList(newSecret));
    this.service.activateNewSecrets("SOME_DEVICE", Arrays.asList(SecretType.E_METER_MASTER_KEY));
    // THEN
    assertThat(newSecret.getSecretStatus()).isEqualTo(SecretStatus.ACTIVE);
  }

  @Test
  public void activateSecretsWithCurrentActiveSecret() {
    // GIVEN
    final DbEncryptedSecret newSecret = new DbEncryptedSecret();
    newSecret.setId(1L);
    newSecret.setSecretStatus(SecretStatus.NEW);
    final DbEncryptedSecret activeSecret = new DbEncryptedSecret();
    newSecret.setId(2L);
    newSecret.setSecretStatus(SecretStatus.ACTIVE);
    // WHEN
    when(this.secretRepository.getSecretCount(
            SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.NEW))
        .thenReturn(1);
    when(this.secretRepository.findSecrets(
            SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.ACTIVE))
        .thenReturn(Arrays.asList(activeSecret));
    when(this.secretRepository.findSecrets(
            SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.NEW))
        .thenReturn(Arrays.asList(newSecret));
    when(this.secretRepository.saveAll(any())).thenReturn(Arrays.asList(newSecret, activeSecret));
    this.service.activateNewSecrets("SOME_DEVICE", Arrays.asList(SecretType.E_METER_MASTER_KEY));
    // THEN
    assertThat(newSecret.getSecretStatus()).isEqualTo(SecretStatus.ACTIVE);
    assertThat(activeSecret.getSecretStatus()).isEqualTo(SecretStatus.EXPIRED);
  }

  @Test
  public void activateSecretsNoNewSecret() {
    // GIVEN
    final DbEncryptedSecret newSecret = new DbEncryptedSecret();
    newSecret.setId(1L);
    newSecret.setSecretStatus(SecretStatus.NEW);
    // WHEN
    when(this.secretRepository.getSecretCount(
            SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.NEW))
        .thenReturn(0);
    assertThatIllegalStateException()
        .isThrownBy(
            () ->
                this.service.activateNewSecrets(
                    "SOME_DEVICE", Arrays.asList(SecretType.E_METER_MASTER_KEY)));
  }

  @Test
  public void hasNewSecret() {
    when(this.secretRepository.getSecretCount(
            SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.NEW))
        .thenReturn(1);
    boolean result = this.service.hasNewSecret(SOME_DEVICE, SecretType.E_METER_MASTER_KEY);
    assertThat(result).isTrue();
    when(this.secretRepository.getSecretCount(
            SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.NEW))
        .thenReturn(0);
    result = this.service.hasNewSecret(SOME_DEVICE, SecretType.E_METER_MASTER_KEY);
    assertThat(result).isFalse();
  }

  @Test
  public void generateAndStoreSecrets() throws EncrypterException {
    final Date now = new Date();
    final String reference = "1";
    final byte[] aesSecret = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    final byte[] secret = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
    final byte[] rsaSecret = {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
    final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
    keyReference.setReference(reference);
    keyReference.setEncryptionProviderType(ENCRYPTION_PROVIDER_TYPE);
    keyReference.setValidFrom(now);
    when(this.keyRepository.findByTypeAndValid(any(), any()))
        .thenReturn(Arrays.asList(keyReference));
    when(this.encryptionDelegate.generateAes128BitsSecret(ENCRYPTION_PROVIDER_TYPE, reference))
        .thenReturn(aesSecret);
    when(this.encryptionDelegate.decrypt(any(), any())).thenReturn(secret);
    when(this.rsaEncrypter.encrypt(any())).thenReturn(rsaSecret);
    final List<TypedSecret> secrets =
        this.service.generateAndStoreSecrets(
            SOME_DEVICE, Arrays.asList(SecretType.E_METER_AUTHENTICATION_KEY));
    assertThat(secrets.size()).isEqualTo(1);
    final TypedSecret typedSecret = secrets.get(0);
    assertThat(typedSecret.getSecretType()).isEqualTo(SecretType.E_METER_AUTHENTICATION_KEY);
    assertThat(typedSecret.getSecret()).isEqualTo(rsaSecret);
  }
}
