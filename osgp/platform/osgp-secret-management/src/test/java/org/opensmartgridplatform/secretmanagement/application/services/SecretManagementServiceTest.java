/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
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
import org.opensmartgridplatform.shared.security.EncryptedSecret;
import org.opensmartgridplatform.shared.security.EncryptionDelegate;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;
import org.opensmartgridplatform.shared.security.Secret;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
public class SecretManagementServiceTest {

    private static final String SOME_DEVICE = "SOME_DEVICE";

    private SecretManagementService service;

    @Mock
    private EncryptionDelegate encryptionDelegate;
    @Mock
    private DbEncryptedSecretRepository secretRepository;
    @Mock
    private DbEncryptionKeyRepository keyRepository;

    @BeforeEach
    public void setHsmEncryption() {
        this.service = new SecretManagementService(this.encryptionDelegate, EncryptionProviderType.HSM,
                this.secretRepository, this.keyRepository);
    }

    @Test
    public void retrieveSecrets() throws Exception {
        //GIVEN
        final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
        final DbEncryptedSecret secret = new DbEncryptedSecret();
        secret.setSecretType(SecretType.E_METER_MASTER_KEY);
        secret.setEncryptionKeyReference(keyReference);
        final List<DbEncryptedSecret> secretList = Arrays.asList(secret);
        final Secret decryptedSecret = new Secret("secret".getBytes());

        //WHEN
        when(this.secretRepository.findSecrets(SOME_DEVICE, SecretType.E_METER_MASTER_KEY,
                SecretStatus.ACTIVE)).thenReturn(secretList);
        when(this.encryptionDelegate.decrypt(any(), any())).thenReturn(decryptedSecret);
        final List<TypedSecret> typedSecrets = this.service.retrieveSecrets(SOME_DEVICE,
                Arrays.asList(SecretType.E_METER_MASTER_KEY));

        //THEN
        assertThat(typedSecrets).isNotNull();
        assertThat(typedSecrets.size()).isEqualTo(1);
        final TypedSecret typedSecret = typedSecrets.get(0);
        assertThat(typedSecret.getSecret()).isEqualTo(HexUtils.toHexString("secret".getBytes()));
        assertThat(typedSecret.getSecretType()).isEqualTo(SecretType.E_METER_MASTER_KEY);
    }

    @Test
    public void retrieveSecrets_decryptionError() {
        //GIVEN
        final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
        final DbEncryptedSecret secret = new DbEncryptedSecret();
        secret.setSecretType(SecretType.E_METER_MASTER_KEY);
        secret.setEncryptionKeyReference(keyReference);
        final List<DbEncryptedSecret> secretPage = Arrays.asList(secret);

        //WHEN
        when(this.secretRepository.findSecrets(SOME_DEVICE, SecretType.E_METER_MASTER_KEY,
                SecretStatus.ACTIVE)).thenReturn(secretPage);
        when(this.encryptionDelegate.decrypt(any(), any())).thenThrow(new RuntimeException("Decryption error"));

        //THEN
        assertThatIllegalStateException().isThrownBy(
                () -> this.service.retrieveSecrets("SOME_DEVICE", Arrays.asList(SecretType.E_METER_MASTER_KEY)));
    }

    @Test
    public void retrieveSecrets_secretWithoutKey() {
        final DbEncryptedSecret secret = new DbEncryptedSecret();
        final List<DbEncryptedSecret> secretList = Arrays.asList(secret);
        when(this.secretRepository.findSecrets(SOME_DEVICE, SecretType.E_METER_MASTER_KEY,
                SecretStatus.ACTIVE)).thenReturn(secretList);
        assertThatIllegalStateException().isThrownBy(
                () -> this.service.retrieveSecrets("SOME_DEVICE", Arrays.asList(SecretType.E_METER_MASTER_KEY)));
    }

    @Test
    public void retrieveSecrets_noSecrets() {
        assertThatIllegalStateException().isThrownBy(
                () -> this.service.retrieveSecrets("SOME_DEVICE", Arrays.asList(SecretType.E_METER_MASTER_KEY)));
    }

    @Test
    public void storeSecrets() throws Exception {
        //GIVEN
        final TypedSecret typedSecret = new TypedSecret();
        typedSecret.setSecretType(SecretType.E_METER_MASTER_KEY);
        typedSecret.setSecret(HexUtils.toHexString("$3cr3t".getBytes()));
        final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
        keyReference.setEncryptionProviderType(EncryptionProviderType.JRE);
        keyReference.setReference("keyReferenceString");
        final EncryptedSecret encryptedSecret = new EncryptedSecret(EncryptionProviderType.JRE, "$3cr3t".getBytes());
        final DbEncryptedSecret dbEncryptedSecret = new DbEncryptedSecret();

        //WHEN
        when(this.secretRepository.getSecretCount(SOME_DEVICE, SecretType.E_METER_MASTER_KEY,
                SecretStatus.NEW)).thenReturn(0);
        when(this.keyRepository.findByTypeAndValid(any(), any(), any())).thenReturn(
                new PageImpl<>(Arrays.asList(keyReference)));
        when(this.encryptionDelegate.encrypt(any(), any(), anyString())).thenReturn(encryptedSecret);
        this.service.storeSecrets("SOME_DEVICE", Arrays.asList(typedSecret));
        //THEN
        final ArgumentCaptor<DbEncryptedSecret> secretArgumentCaptor = ArgumentCaptor.forClass(DbEncryptedSecret.class);
        verify(this.secretRepository).save(secretArgumentCaptor.capture());
        final DbEncryptedSecret savedSecret = secretArgumentCaptor.getValue();
        assertThat(savedSecret).isNotNull();
        assertThat(savedSecret.getDeviceIdentification()).isEqualTo("SOME_DEVICE");
        assertThat(savedSecret.getSecretType()).isEqualTo(typedSecret.getSecretType());
        assertThat(savedSecret.getEncodedSecret()).isEqualTo(HexUtils.toHexString(encryptedSecret.getSecret()));
        assertThat(savedSecret.getEncryptionKeyReference()).isEqualTo(keyReference);
        assertThat(savedSecret.getCreationTime()).isNotNull();
    }

    @Test
    public void storeSecrets_newKeyAlreadyStored() throws Exception {
        //GIVEN
        final TypedSecret typedSecret = new TypedSecret();
        typedSecret.setSecretType(SecretType.E_METER_MASTER_KEY);
        //WHEN
        when(this.secretRepository.getSecretCount(SOME_DEVICE, SecretType.E_METER_MASTER_KEY,
                SecretStatus.NEW)).thenReturn(1);
        //THEN
        assertThatIllegalStateException().isThrownBy(
                () -> this.service.storeSecrets("SOME_DEVICE", Arrays.asList(typedSecret)));
    }

    @Test
    public void storeSecrets_secretNull() {
        //GIVEN
        final TypedSecret typedSecret = new TypedSecret();
        typedSecret.setSecretType(SecretType.E_METER_MASTER_KEY);
        final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
        keyReference.setEncryptionProviderType(EncryptionProviderType.JRE);
        keyReference.setReference("keyReferenceString");
        //THEN
        assertThatIllegalArgumentException().isThrownBy(
                () -> this.service.storeSecrets("SOME_DEVICE", Arrays.asList(typedSecret)));
    }

    @Test
    public void storeSecrets_secretTypeNull() {
        //GIVEN
        final TypedSecret typedSecret = new TypedSecret();
        typedSecret.setSecret(HexUtils.toHexString("$3cr3t".getBytes()));
        final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
        keyReference.setEncryptionProviderType(EncryptionProviderType.JRE);
        keyReference.setReference("keyReferenceString");
        //THEN
        assertThatIllegalArgumentException().isThrownBy(
                () -> this.service.storeSecrets("SOME_DEVICE", Arrays.asList(typedSecret)));
    }

    @Test
    public void storeSecrets_noKey() {
        //GIVEN
        final TypedSecret typedSecret = new TypedSecret();
        typedSecret.setSecretType(SecretType.E_METER_MASTER_KEY);
        typedSecret.setSecret(HexUtils.toHexString("$3cr3t".getBytes()));
        //WHEN
        when(this.secretRepository.getSecretCount(SOME_DEVICE, SecretType.E_METER_MASTER_KEY,
                SecretStatus.NEW)).thenReturn(0);
        when(this.keyRepository.findByTypeAndValid(any(), any(), any())).thenReturn(Page.empty());
        //THEN
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(
                () -> this.service.storeSecrets("SOME_DEVICE", Arrays.asList(typedSecret)));

    }

    @Test
    public void storeSecrets_multipleKeys() {
        //GIVEN
        final TypedSecret typedSecret = new TypedSecret();
        typedSecret.setSecretType(SecretType.E_METER_MASTER_KEY);
        typedSecret.setSecret(HexUtils.toHexString("$3cr3t".getBytes()));
        //WHEN
        when(this.secretRepository.getSecretCount(SOME_DEVICE, SecretType.E_METER_MASTER_KEY,
                SecretStatus.NEW)).thenReturn(0);
        when(this.keyRepository.findByTypeAndValid(any(), any(), any())).thenReturn(
                new PageImpl<>(Arrays.asList(new DbEncryptionKeyReference(), new DbEncryptionKeyReference())));
        //THEN
        try {
            this.service.storeSecrets("SOME_DEVICE", Arrays.asList(typedSecret));
        } catch (final Exception exc) {

        }
        assertThatIllegalStateException().isThrownBy(
                () -> this.service.storeSecrets("SOME_DEVICE", Arrays.asList(typedSecret)));

    }

    @Test
    public void storeSecrets_encryptionError() {
        //GIVEN
        final TypedSecret typedSecret = new TypedSecret();
        typedSecret.setSecretType(SecretType.E_METER_MASTER_KEY);
        typedSecret.setSecret(HexUtils.toHexString("$3cr3t".getBytes()));
        final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
        keyReference.setEncryptionProviderType(EncryptionProviderType.JRE);
        keyReference.setReference("keyReferenceString");

        //WHEN
        when(this.secretRepository.getSecretCount(SOME_DEVICE, SecretType.E_METER_MASTER_KEY,
                SecretStatus.NEW)).thenReturn(0);
        when(this.keyRepository.findByTypeAndValid(any(), any(), any())).thenReturn(
                new PageImpl<>(Arrays.asList(keyReference)));
        when(this.encryptionDelegate.encrypt(any(), any(), anyString())).thenThrow(
                new RuntimeException("Encryption error"));
        //THEN
        assertThatIllegalStateException().isThrownBy(
                () -> this.service.storeSecrets("SOME_DEVICE", Arrays.asList(typedSecret)));

    }

    @Test
    public void storeSecrets_existingSecret() throws Exception {
        //GIVEN
        final TypedSecret typedSecret = new TypedSecret();
        typedSecret.setSecretType(SecretType.E_METER_MASTER_KEY);
        typedSecret.setSecret(HexUtils.toHexString("$3cr3t".getBytes()));
        final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
        keyReference.setEncryptionProviderType(EncryptionProviderType.JRE);
        keyReference.setReference("keyReferenceString");
        final EncryptedSecret encryptedSecret = new EncryptedSecret(EncryptionProviderType.JRE, "$3cr3t".getBytes());
        final DbEncryptedSecret existingDbSecret = new DbEncryptedSecret();
        existingDbSecret.setCreationTime(new Date());
        existingDbSecret.setSecretType(SecretType.E_METER_MASTER_KEY);
        existingDbSecret.setEncodedSecret("1234567890ABCDEF");
        existingDbSecret.setDeviceIdentification("SOME_DEVICE");
        existingDbSecret.setEncryptionKeyReference(keyReference);

        //WHEN
        when(this.secretRepository.getSecretCount(SOME_DEVICE, SecretType.E_METER_MASTER_KEY,
                SecretStatus.NEW)).thenReturn(0);
        //when(this.secretRepository.findById(1L)).thenReturn(Optional.of(existingDbSecret));
        when(this.keyRepository.findByTypeAndValid(any(), any(), any())).thenReturn(
                new PageImpl<>(Arrays.asList(keyReference)));
        //when(this.encryptionDelegate.decrypt(any(), any())).thenReturn(
        //        new Secret(existingDbSecret.getEncodedSecret().getBytes()));    //decrypt existing DB secret
        when(this.encryptionDelegate.encrypt(any(), any(), anyString())).thenReturn(
                encryptedSecret);   //encrypt new DB secret
        this.service.storeSecrets("SOME_DEVICE", Arrays.asList(typedSecret));
        //THEN
        final ArgumentCaptor<DbEncryptedSecret> secretArgumentCaptor = ArgumentCaptor.forClass(DbEncryptedSecret.class);
        verify(this.secretRepository).save(secretArgumentCaptor.capture());
        final DbEncryptedSecret savedSecret = secretArgumentCaptor.getValue();
        assertThat(savedSecret).isNotNull();
        assertThat(savedSecret.getDeviceIdentification()).isEqualTo("SOME_DEVICE");
        assertThat(savedSecret.getSecretType()).isEqualTo(typedSecret.getSecretType());
        assertThat(savedSecret.getEncodedSecret()).isEqualTo(HexUtils.toHexString(encryptedSecret.getSecret()));
        assertThat(savedSecret.getEncryptionKeyReference()).isEqualTo(keyReference);
        assertThat(savedSecret.getCreationTime()).isNotNull();
    }

    @Test
    public void storeSecrets_existingIdenticalSecret() {
        //GIVEN
        final TypedSecret newTypedSecret = new TypedSecret();
        newTypedSecret.setSecretType(SecretType.E_METER_MASTER_KEY);
        newTypedSecret.setSecret(HexUtils.toHexString("$3cr3t".getBytes()));
        final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
        keyReference.setEncryptionProviderType(EncryptionProviderType.JRE);
        keyReference.setReference("keyReferenceString");
        final DbEncryptedSecret existingDbSecret = new DbEncryptedSecret();
        existingDbSecret.setCreationTime(new Date());
        existingDbSecret.setSecretType(SecretType.E_METER_MASTER_KEY);
        existingDbSecret.setEncodedSecret("1234567890ABCDEF");  //value doesnt matter because of decryption mock
        existingDbSecret.setDeviceIdentification("SOME_DEVICE");
        existingDbSecret.setEncryptionKeyReference(keyReference);

        //WHEN
        when(this.secretRepository.getSecretCount(SOME_DEVICE, SecretType.E_METER_MASTER_KEY,
                SecretStatus.NEW)).thenReturn(0);
        when(this.secretRepository.findSecrets(SOME_DEVICE, SecretType.E_METER_MASTER_KEY,
                SecretStatus.ACTIVE)).thenReturn(Arrays.asList(existingDbSecret));
        when(this.encryptionDelegate.decrypt(any(), any())).thenReturn(
                new Secret(HexUtils.fromHexString(newTypedSecret.getSecret())));    //identical secrets

        //THEN
        assertThatIllegalArgumentException().isThrownBy(
                () -> this.service.storeSecrets("SOME_DEVICE", Arrays.asList(newTypedSecret))).withMessageContaining(
                "identical to current");
    }

    @Test
    public void activateSecrets_noCurrentActiveSecret() {
        //GIVEN
        final DbEncryptedSecret newSecret = new DbEncryptedSecret();
        newSecret.setId(1L);
        newSecret.setSecretStatus(SecretStatus.NEW);
        //WHEN
        when(this.secretRepository.getSecretCount(SOME_DEVICE, SecretType.E_METER_MASTER_KEY,
                SecretStatus.NEW)).thenReturn(1);
        when(this.secretRepository.findSecrets(SOME_DEVICE, SecretType.E_METER_MASTER_KEY,
                SecretStatus.ACTIVE)).thenReturn(new ArrayList<>());
        when(this.secretRepository.findSecrets(SOME_DEVICE, SecretType.E_METER_MASTER_KEY,
                SecretStatus.NEW)).thenReturn(Arrays.asList(newSecret));
        when(this.secretRepository.save(newSecret)).thenReturn(newSecret);
        this.service.activateNewSecrets("SOME_DEVICE", Arrays.asList(SecretType.E_METER_MASTER_KEY));
        //THEN
        assertThat(newSecret.getSecretStatus()).isEqualTo(SecretStatus.ACTIVE);
    }

    @Test
    public void activateSecrets_withCurrentActiveSecret() {
        //GIVEN
        final DbEncryptedSecret newSecret = new DbEncryptedSecret();
        newSecret.setId(1L);
        newSecret.setSecretStatus(SecretStatus.NEW);
        final DbEncryptedSecret activeSecret = new DbEncryptedSecret();
        newSecret.setId(2L);
        newSecret.setSecretStatus(SecretStatus.ACTIVE);
        //WHEN
        when(this.secretRepository.getSecretCount(SOME_DEVICE, SecretType.E_METER_MASTER_KEY,
                SecretStatus.NEW)).thenReturn(1);
        when(this.secretRepository.findSecrets(SOME_DEVICE, SecretType.E_METER_MASTER_KEY,
                SecretStatus.ACTIVE)).thenReturn(Arrays.asList(activeSecret));
        when(this.secretRepository.findSecrets(SOME_DEVICE, SecretType.E_METER_MASTER_KEY,
                SecretStatus.NEW)).thenReturn(Arrays.asList(newSecret));
        when(this.secretRepository.save(newSecret)).thenReturn(newSecret);
        when(this.secretRepository.save(activeSecret)).thenReturn(activeSecret);
        this.service.activateNewSecrets("SOME_DEVICE", Arrays.asList(SecretType.E_METER_MASTER_KEY));
        //THEN
        assertThat(newSecret.getSecretStatus()).isEqualTo(SecretStatus.ACTIVE);
        assertThat(activeSecret.getSecretStatus()).isEqualTo(SecretStatus.EXPIRED);
    }

    @Test
    public void hasNewSecret() {
        when(this.secretRepository.getSecretCount(SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.NEW)).thenReturn(1);
        boolean result = this.service.hasNewSecret(SOME_DEVICE, SecretType.E_METER_MASTER_KEY);
        assertThat(result).isEqualTo(true);
        when(this.secretRepository.getSecretCount(SOME_DEVICE, SecretType.E_METER_MASTER_KEY, SecretStatus.NEW)).thenReturn(0);
        result = this.service.hasNewSecret(SOME_DEVICE, SecretType.E_METER_MASTER_KEY);
        assertThat(result).isEqualTo(false);
    }
}
