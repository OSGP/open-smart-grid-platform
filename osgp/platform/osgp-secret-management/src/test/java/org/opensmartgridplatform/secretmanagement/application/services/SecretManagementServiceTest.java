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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
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
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
public class SecretManagementServiceTest {

  private static final String SOME_DEVICE = "SOME_DEVICE";
  private static final EncryptionProviderType ENCRYPTION_PROVIDER_TYPE = EncryptionProviderType.HSM;

  private SecretManagementService service;

  @Mock private Appender<ILoggingEvent> mockAppender;
  @Mock private EncryptionDelegate encryptionDelegate;
  @Mock private DbEncryptedSecretRepository secretRepository;
  @Mock private DbEncryptionKeyRepository keyRepository;
  @Mock private RsaEncrypter encrypterForSecretManagementClient;
  @Mock private RsaEncrypter decrypterForSecretManagement;

  @BeforeEach
  public void setUpSecretManagementService() {
    this.service =
        new SecretManagementService(
            this.encryptionDelegate,
            ENCRYPTION_PROVIDER_TYPE,
            this.secretRepository,
            this.keyRepository,
            this.encrypterForSecretManagementClient,
            this.decrypterForSecretManagement);
    final Logger logger = (Logger) LoggerFactory.getLogger(SecretManagementService.class.getName());
    logger.addAppender(this.mockAppender);
  }

  @Test
  public void retrieveSecrets() throws Exception {

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

    when(this.secretRepository.findSecrets(
            SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.ACTIVE))
        .thenReturn(secretList);
    when(this.encryptionDelegate.decrypt(any(), any())).thenReturn(decryptedSecret);
    when(this.encrypterForSecretManagementClient.encrypt(any())).thenReturn(rsaSecret);
    final List<TypedSecret> typedSecrets =
        this.service.retrieveSecrets(SOME_DEVICE, Arrays.asList(SecretType.E_METER_MASTER_KEY));

    assertThat(typedSecrets).isNotNull();
    assertThat(typedSecrets.size()).isEqualTo(1);
    final TypedSecret typedSecret = typedSecrets.get(0);
    assertThat(typedSecret.getSecret()).isEqualTo(rsaSecret);
    assertThat(typedSecret.getSecretType()).isEqualTo(SecretType.E_METER_MASTER_KEY);
  }

  @Test
  public void retrieveSecretsDecryptionError() throws EncrypterException {

    final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
    keyReference.setEncryptionProviderType(ENCRYPTION_PROVIDER_TYPE);
    keyReference.setReference("1");
    final DbEncryptedSecret secret = new DbEncryptedSecret();
    secret.setSecretType(SecretType.E_METER_MASTER_KEY);
    secret.setEncryptionKeyReference(keyReference);
    secret.setEncodedSecret("0123456789ABCDEF0123456789ABCDEF");
    final List<DbEncryptedSecret> secretPage = Arrays.asList(secret);

    when(this.secretRepository.findSecrets(
            SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.ACTIVE))
        .thenReturn(secretPage);
    when(this.encryptionDelegate.decrypt(any(), any()))
        .thenThrow(new EncrypterException("Decryption error"));

    assertThatIllegalStateException()
        .isThrownBy(
            () ->
                this.service.retrieveSecrets(
                    SOME_DEVICE, Arrays.asList(SecretType.E_METER_MASTER_KEY)));
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
                    SOME_DEVICE, Arrays.asList(SecretType.E_METER_MASTER_KEY)));
  }

  @Test
  public void retrieveSecretsNoSecrets() {
    final List<SecretType> secretTypes = Arrays.asList(SecretType.E_METER_MASTER_KEY);
    final List<TypedSecret> result = this.service.retrieveSecrets(SOME_DEVICE, secretTypes);
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(secretTypes.size());
    assertThat(result.get(0)).isNotNull();
    assertThat(result.get(0).getSecretType()).isEqualTo(SecretType.E_METER_MASTER_KEY);
    assertThat(result.get(0).getSecret()).isNull();
  }

  @Test
  public void storeSecrets() throws Exception {

    final TypedSecret typedSecret =
        new TypedSecret("rsaSecret00000001".getBytes(), SecretType.E_METER_ENCRYPTION_KEY_UNICAST);
    final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
    keyReference.setEncryptionProviderType(ENCRYPTION_PROVIDER_TYPE);
    keyReference.setReference("1");
    final EncryptedSecret encryptedSecret =
        new EncryptedSecret(ENCRYPTION_PROVIDER_TYPE, "aesSecret0000001".getBytes());

    when(this.keyRepository.findByTypeAndValid(any(), any()))
        .thenReturn(Arrays.asList(keyReference));
    when(this.keyRepository.findByTypeAndReference(ENCRYPTION_PROVIDER_TYPE, "1"))
        .thenReturn(keyReference);
    when(this.encryptionDelegate.encrypt(any(), any(), anyString())).thenReturn(encryptedSecret);

    this.service.storeSecrets(SOME_DEVICE, Arrays.asList(typedSecret));

    final ArgumentCaptor<List<DbEncryptedSecret>> secretListArgumentCaptor =
        this.getListOfDbEncryptedSecretsArgumentCaptor();
    verify(this.secretRepository).saveAll(secretListArgumentCaptor.capture());
    final List<DbEncryptedSecret> savedSecrets = secretListArgumentCaptor.getValue();
    assertThat(savedSecrets).isNotNull();
    assertThat(savedSecrets.size()).isEqualTo(1);
    final DbEncryptedSecret savedSecret = savedSecrets.get(0);
    assertThat(savedSecret.getDeviceIdentification()).isEqualTo(SOME_DEVICE);
    assertThat(savedSecret.getSecretType()).isEqualTo(typedSecret.getSecretType());
    assertThat(savedSecret.getEncodedSecret())
        .isEqualTo(HexUtils.toHexString(encryptedSecret.getSecret()));
    assertThat(savedSecret.getEncryptionKeyReference()).isEqualTo(keyReference);
    assertThat(savedSecret.getCreationTime()).isNotNull();
  }

  private DbEncryptedSecret getSecret(final SecretType secretType, final int minutesOld) {
    final DbEncryptedSecret secret = new DbEncryptedSecret();
    secret.setDeviceIdentification(SOME_DEVICE);
    secret.setSecretStatus(SecretStatus.NEW);
    secret.setSecretType(secretType);
    secret.setCreationTime(new Date(System.currentTimeMillis() - (minutesOld * 60000L)));
    secret.setEncodedSecret("1234567890abcdef");
    final DbEncryptionKeyReference encryptionKeyReference = new DbEncryptionKeyReference();
    encryptionKeyReference.setEncryptionProviderType(EncryptionProviderType.HSM);
    encryptionKeyReference.setReference("1");
    secret.setEncryptionKeyReference(encryptionKeyReference);
    return secret;
  }

  @Test
  public void storeSecretWhenNewSecretAlreadyExists() throws Exception {

    final TypedSecret typedSecret =
        new TypedSecret("rsaSecret00000001".getBytes(), SecretType.E_METER_ENCRYPTION_KEY_UNICAST);
    final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
    keyReference.setEncryptionProviderType(ENCRYPTION_PROVIDER_TYPE);
    keyReference.setReference("1");
    final EncryptedSecret encryptedSecret =
        new EncryptedSecret(ENCRYPTION_PROVIDER_TYPE, "aesSecret0000001".getBytes());
    final DbEncryptedSecret dbEncryptedSecret =
        this.getSecret(SecretType.E_METER_ENCRYPTION_KEY_UNICAST, 100);

    when(this.keyRepository.findByTypeAndValid(any(), any()))
        .thenReturn(Arrays.asList(keyReference));
    when(this.keyRepository.findByTypeAndReference(ENCRYPTION_PROVIDER_TYPE, "1"))
        .thenReturn(keyReference);
    when(this.secretRepository.findSecrets(
            SOME_DEVICE, SecretType.E_METER_ENCRYPTION_KEY_UNICAST, SecretStatus.NEW))
        .thenReturn(Arrays.asList(dbEncryptedSecret));
    when(this.encryptionDelegate.encrypt(any(), any(), anyString())).thenReturn(encryptedSecret);

    this.service.storeSecrets(SOME_DEVICE, Arrays.asList(typedSecret));

    final ArgumentCaptor<List<DbEncryptedSecret>> secretListArgumentCaptor =
        this.getListOfDbEncryptedSecretsArgumentCaptor();
    verify(this.secretRepository).saveAll(secretListArgumentCaptor.capture());
    final List<DbEncryptedSecret> savedSecrets = secretListArgumentCaptor.getValue();

    final ArgumentCaptor<DbEncryptedSecret> secretToWithdrawArgumentCaptor =
        ArgumentCaptor.forClass(DbEncryptedSecret.class);
    verify(this.secretRepository, times(1)).save(secretToWithdrawArgumentCaptor.capture());
    final DbEncryptedSecret savedSecretToWithdraw = secretToWithdrawArgumentCaptor.getValue();

    assertThat(savedSecrets).isNotNull();
    assertThat(savedSecrets.size()).isEqualTo(1);
    final DbEncryptedSecret savedSecret = savedSecrets.get(0);

    assertThat(savedSecret.getDeviceIdentification()).isEqualTo(SOME_DEVICE);
    assertThat(savedSecret.getSecretType()).isEqualTo(typedSecret.getSecretType());
    assertThat(savedSecret.getEncryptionKeyReference()).isEqualTo(keyReference);
    assertThat(savedSecret.getCreationTime()).isNotNull();

    assertThat(savedSecret.getEncodedSecret())
        .isEqualTo(HexUtils.toHexString(encryptedSecret.getSecret()));
    assertThat(savedSecret.getSecretStatus()).isEqualTo(SecretStatus.NEW);

    assertThat(savedSecretToWithdraw.getDeviceIdentification()).isEqualTo(SOME_DEVICE);
    assertThat(savedSecretToWithdraw.getSecretType()).isEqualTo(typedSecret.getSecretType());
    assertThat(savedSecretToWithdraw.getEncryptionKeyReference())
        .isEqualTo(dbEncryptedSecret.getEncryptionKeyReference());
    assertThat(savedSecretToWithdraw.getCreationTime()).isNotNull();

    assertThat(savedSecretToWithdraw.getEncodedSecret())
        .isEqualTo(dbEncryptedSecret.getEncodedSecret());
    assertThat(savedSecretToWithdraw.getSecretStatus()).isEqualTo(SecretStatus.WITHDRAWN);
  }

  @Test
  public void storeSecretsNoKey() {

    final TypedSecret typedSecret =
        new TypedSecret("$3cr3t".getBytes(), SecretType.E_METER_MASTER_KEY);

    when(this.keyRepository.findByTypeAndValid(any(), any())).thenReturn(Arrays.asList());

    assertThatExceptionOfType(NoSuchElementException.class)
        .isThrownBy(() -> this.service.storeSecrets(SOME_DEVICE, Arrays.asList(typedSecret)));
  }

  @Test
  public void storeSecretsMultipleKeys() {

    final TypedSecret typedSecret =
        new TypedSecret("$3cr3t".getBytes(), SecretType.E_METER_MASTER_KEY);

    when(this.keyRepository.findByTypeAndValid(any(), any()))
        .thenReturn(Arrays.asList(new DbEncryptionKeyReference(), new DbEncryptionKeyReference()));

    assertThatIllegalStateException()
        .isThrownBy(() -> this.service.storeSecrets(SOME_DEVICE, Arrays.asList(typedSecret)));
  }

  @Test
  public void storeSecretsEncryptionError() throws EncrypterException {

    final TypedSecret typedSecret =
        new TypedSecret("$3cr3t".getBytes(), SecretType.E_METER_MASTER_KEY);
    final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
    keyReference.setEncryptionProviderType(ENCRYPTION_PROVIDER_TYPE);
    keyReference.setReference("keyReferenceString");

    when(this.keyRepository.findByTypeAndValid(any(), any()))
        .thenReturn(Arrays.asList(keyReference));
    when(this.encryptionDelegate.encrypt(any(), any(), anyString()))
        .thenThrow(new EncrypterException("Encryption error"));

    assertThatIllegalStateException()
        .isThrownBy(() -> this.service.storeSecrets(SOME_DEVICE, Arrays.asList(typedSecret)));
  }

  @Test
  public void storeSecretsExistingSecret() throws Exception {

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
    existingDbSecret.setDeviceIdentification(SOME_DEVICE);
    existingDbSecret.setEncryptionKeyReference(keyReference);

    when(this.keyRepository.findByTypeAndValid(any(), any()))
        .thenReturn(Arrays.asList(keyReference));
    when(this.keyRepository.findByTypeAndReference(ENCRYPTION_PROVIDER_TYPE, "1"))
        .thenReturn(keyReference);
    when(this.encryptionDelegate.encrypt(any(), any(), anyString()))
        .thenReturn(encryptedSecret); // encrypt new DB secret

    this.service.storeSecrets(SOME_DEVICE, Arrays.asList(typedSecret));

    final ArgumentCaptor<List<DbEncryptedSecret>> secretListArgumentCaptor =
        this.getListOfDbEncryptedSecretsArgumentCaptor();
    verify(this.secretRepository).saveAll(secretListArgumentCaptor.capture());
    final List<DbEncryptedSecret> savedSecrets = secretListArgumentCaptor.getValue();
    assertThat(savedSecrets).isNotNull();
    assertThat(savedSecrets.size()).isEqualTo(1);
    final DbEncryptedSecret savedSecret = savedSecrets.get(0);
    assertThat(savedSecret).isNotNull();
    assertThat(savedSecret.getDeviceIdentification()).isEqualTo(SOME_DEVICE);
    assertThat(savedSecret.getSecretType()).isEqualTo(typedSecret.getSecretType());
    assertThat(savedSecret.getEncodedSecret())
        .isEqualTo(HexUtils.toHexString(encryptedSecret.getSecret()));
    assertThat(savedSecret.getEncryptionKeyReference()).isEqualTo(keyReference);
    assertThat(savedSecret.getCreationTime()).isNotNull();
  }

  @Test
  public void activateSecretsNoCurrentActiveSecret() {

    final DbEncryptedSecret newSecret = new DbEncryptedSecret();
    newSecret.setId(1L);
    newSecret.setSecretType(SecretType.E_METER_MASTER_KEY);
    newSecret.setSecretStatus(SecretStatus.NEW);

    when(this.secretRepository.findSecrets(
            SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.NEW))
        .thenReturn(Arrays.asList(newSecret));
    when(this.secretRepository.saveAll(Arrays.asList(newSecret)))
        .thenReturn(Arrays.asList(newSecret));
    this.service.activateNewSecrets(SOME_DEVICE, Arrays.asList(SecretType.E_METER_MASTER_KEY));

    assertThat(newSecret.getSecretStatus()).isEqualTo(SecretStatus.ACTIVE);

    final ArgumentCaptor<List<DbEncryptedSecret>> secretListArgumentCaptor =
        this.getListOfDbEncryptedSecretsArgumentCaptor();
    verify(this.secretRepository).saveAll(secretListArgumentCaptor.capture());
    final List<DbEncryptedSecret> savedSecrets = secretListArgumentCaptor.getValue();
    assertThat(savedSecrets).isNotNull();
    assertThat(savedSecrets.size()).isEqualTo(1);
    final DbEncryptedSecret savedSecret = savedSecrets.get(0);
    assertThat(savedSecret.getSecretType()).isEqualTo(newSecret.getSecretType());
    assertThat(savedSecret.getSecretStatus()).isEqualTo(SecretStatus.ACTIVE);
  }

  @Test
  public void activateSecretsWithCurrentActiveSecret() {

    final DbEncryptedSecret newSecret = new DbEncryptedSecret();
    newSecret.setId(1L);
    newSecret.setSecretStatus(SecretStatus.NEW);
    final DbEncryptedSecret activeSecret = new DbEncryptedSecret();
    newSecret.setId(2L);
    newSecret.setSecretStatus(SecretStatus.ACTIVE);

    when(this.secretRepository.findSecrets(
            SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.ACTIVE))
        .thenReturn(Arrays.asList(activeSecret));
    when(this.secretRepository.findSecrets(
            SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.NEW))
        .thenReturn(Arrays.asList(newSecret));
    when(this.secretRepository.saveAll(any())).thenReturn(Arrays.asList(newSecret, activeSecret));
    this.service.activateNewSecrets(SOME_DEVICE, Arrays.asList(SecretType.E_METER_MASTER_KEY));

    assertThat(newSecret.getSecretStatus()).isEqualTo(SecretStatus.ACTIVE);
    assertThat(activeSecret.getSecretStatus()).isEqualTo(SecretStatus.EXPIRED);
  }

  @Test
  public void activateSecretsNoNewSecret() {

    this.service.activateNewSecrets("SOME_DEVICE", Arrays.asList(SecretType.E_METER_MASTER_KEY));

    final ArgumentCaptor<List<DbEncryptedSecret>> secretListArgumentCaptor =
        this.getListOfDbEncryptedSecretsArgumentCaptor();
    verify(this.secretRepository).saveAll(secretListArgumentCaptor.capture());
    final List<DbEncryptedSecret> savedSecrets = secretListArgumentCaptor.getValue();
    assertThat(savedSecrets.size()).isEqualTo(0);
  }

  @SuppressWarnings("unchecked")
  private ArgumentCaptor<List<DbEncryptedSecret>> getListOfDbEncryptedSecretsArgumentCaptor() {
    return ArgumentCaptor.forClass(List.class);
  }

  @Test
  public void hasNewSecret() {

    when(this.secretRepository.getSecretCount(
            SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.NEW))
        .thenReturn(1);

    final boolean result = this.service.hasNewSecret(SOME_DEVICE, SecretType.E_METER_MASTER_KEY);

    assertThat(result).isTrue();
  }

  @Test
  public void hasNoNewSecret() {

    when(this.secretRepository.getSecretCount(
            SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.NEW))
        .thenReturn(0);

    final boolean result = this.service.hasNewSecret(SOME_DEVICE, SecretType.E_METER_MASTER_KEY);

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
    when(this.encrypterForSecretManagementClient.encrypt(any())).thenReturn(rsaSecret);
    final List<TypedSecret> secrets =
        this.service.generateAndStoreSecrets(
            SOME_DEVICE, Arrays.asList(SecretType.E_METER_AUTHENTICATION_KEY));
    assertThat(secrets.size()).isEqualTo(1);
    final TypedSecret typedSecret = secrets.get(0);
    assertThat(typedSecret.getSecretType()).isEqualTo(SecretType.E_METER_AUTHENTICATION_KEY);
    assertThat(typedSecret.getSecret()).isEqualTo(rsaSecret);
  }

  @Test
  public void generateAndStoreSecretWhenNewSecretAlreadyExists() throws Exception {

    final Date now = new Date();
    final String reference = "1";
    final byte[] aesSecret = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    final byte[] secret = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
    final byte[] rsaSecret = {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};

    final DbEncryptedSecret dbEncryptedSecret =
        this.getSecret(SecretType.E_METER_ENCRYPTION_KEY_UNICAST, 100);
    final Date originalCreationTime = dbEncryptedSecret.getCreationTime();
    final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
    keyReference.setReference(reference);
    keyReference.setEncryptionProviderType(ENCRYPTION_PROVIDER_TYPE);
    keyReference.setValidFrom(now);

    when(this.keyRepository.findByTypeAndValid(any(), any()))
        .thenReturn(Arrays.asList(keyReference));
    when(this.encryptionDelegate.generateAes128BitsSecret(ENCRYPTION_PROVIDER_TYPE, reference))
        .thenReturn(aesSecret);
    when(this.secretRepository.findSecrets(
            SOME_DEVICE, SecretType.E_METER_ENCRYPTION_KEY_UNICAST, SecretStatus.NEW))
        .thenReturn(Arrays.asList(dbEncryptedSecret));

    when(this.encryptionDelegate.decrypt(any(), any())).thenReturn(secret);
    when(this.encrypterForSecretManagementClient.encrypt(any())).thenReturn(rsaSecret);

    final SecretType secretType = SecretType.E_METER_ENCRYPTION_KEY_UNICAST;
    this.service.generateAndStoreSecrets(SOME_DEVICE, Arrays.asList(secretType));

    final List<DbEncryptedSecret> foundSecrets =
        this.secretRepository.findSecrets(
            SOME_DEVICE, SecretType.E_METER_ENCRYPTION_KEY_UNICAST, SecretStatus.NEW);
    assertThat(foundSecrets).hasSize(1);

    verify(this.secretRepository, never()).saveAll(Arrays.asList(dbEncryptedSecret));
    assertThat(dbEncryptedSecret.getCreationTime()).isEqualTo(originalCreationTime);
    assertThat(dbEncryptedSecret.getSecretStatus()).isEqualTo(SecretStatus.WITHDRAWN);
  }

  @Test
  public void generateAndStoreSecretsWhenNewSecretsAlreadyExists() throws Exception {

    final Date now = new Date();
    final String reference = "1";
    final byte[] aesSecret = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    final byte[] secret = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
    final byte[] rsaSecret = {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
    final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
    keyReference.setReference(reference);
    keyReference.setEncryptionProviderType(ENCRYPTION_PROVIDER_TYPE);
    keyReference.setValidFrom(now);

    final DbEncryptedSecret secretOldEncryption =
        this.getSecret(SecretType.E_METER_ENCRYPTION_KEY_UNICAST, 100);
    final DbEncryptedSecret secretOlderEncryption =
        this.getSecret(SecretType.E_METER_ENCRYPTION_KEY_UNICAST, 1000);
    final Date originalCreationTimeEncryptionSecret = secretOldEncryption.getCreationTime();

    final Date olderCreationTime = secretOlderEncryption.getCreationTime();

    when(this.keyRepository.findByTypeAndValid(any(), any()))
        .thenReturn(Arrays.asList(keyReference));
    when(this.encryptionDelegate.generateAes128BitsSecret(ENCRYPTION_PROVIDER_TYPE, reference))
        .thenReturn(aesSecret);
    when(this.secretRepository.findSecrets(
            SOME_DEVICE, SecretType.E_METER_ENCRYPTION_KEY_UNICAST, SecretStatus.NEW))
        .thenReturn(Arrays.asList(secretOldEncryption, secretOlderEncryption));

    when(this.encryptionDelegate.decrypt(any(), any())).thenReturn(secret);
    when(this.encrypterForSecretManagementClient.encrypt(any())).thenReturn(rsaSecret);

    final SecretType encryptionSecretType = SecretType.E_METER_ENCRYPTION_KEY_UNICAST;
    final SecretType authenticationSecretType = SecretType.E_METER_AUTHENTICATION_KEY;

    this.service.generateAndStoreSecrets(
        SOME_DEVICE, Arrays.asList(encryptionSecretType, authenticationSecretType));

    final String logMessage =
        "During (GenerateOr)Replace Key Process one or more keys with status NEW";
    verify(this.mockAppender, times(2))
        .doAppend(
            argThat(
                argument -> {
                  assertThat(argument.getMessage()).startsWith(logMessage);
                  assertThat(argument.getLevel()).isEqualTo(Level.WARN);
                  return true;
                }));

    verify(this.secretRepository, never()).saveAll(Arrays.asList(secretOldEncryption));
    verify(this.secretRepository, never()).saveAll(Arrays.asList(secretOldEncryption));

    assertThat(secretOldEncryption.getCreationTime())
        .isEqualTo(originalCreationTimeEncryptionSecret);
    assertThat(secretOldEncryption.getSecretStatus()).isEqualTo(SecretStatus.WITHDRAWN);
    assertThat(secretOlderEncryption.getCreationTime().getTime())
        .isEqualTo(olderCreationTime.getTime());
    assertThat(secretOlderEncryption.getSecretStatus()).isEqualTo(SecretStatus.WITHDRAWN);
  }
}
