package org.opensmartgridplatform.secretmgmt.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.apache.tomcat.util.buf.HexUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmgmt.application.domain.SecretType;
import org.opensmartgridplatform.secretmgmt.application.domain.TypedSecret;
import org.opensmartgridplatform.secretmgmt.application.repository.DbEncryptedSecretRepository;
import org.opensmartgridplatform.secretmgmt.application.repository.DbEncryptionKeyRepository;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.EncryptedSecret;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.EncryptionDelegate;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.Secret;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProviderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
public class SecretManagementServiceTest {
    @InjectMocks
    SecretManagementService service;
    @Mock
    EncryptionDelegate encryptionDelegate;
    @Mock
    DbEncryptedSecretRepository secretRepository;
    @Mock
    DbEncryptionKeyRepository keyRepository;

    @Test
    public void retrieveSecrets() throws Exception {
        //GIVEN
        final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
        final DbEncryptedSecret secret = new DbEncryptedSecret();
        secret.setSecretType(SecretType.E_METER_MASTER_KEY);
        secret.setEncryptionKeyReference(keyReference);
        final Secret decryptedSecret = new Secret("secret".getBytes());

        //WHEN
        when(this.secretRepository.findValidOrderedByKeyValidFrom(anyString(), any(), any(), any(), any())).thenReturn(
                new PageImpl<>(Arrays.asList(secret)));
        when(this.encryptionDelegate.decrypt(any(), any())).thenReturn(decryptedSecret);
        final List<TypedSecret> typedSecrets = this.service.retrieveSecrets("SOME_DEVICE",
                Arrays.asList(SecretType.E_METER_MASTER_KEY));

        //THEN
        assertThat(typedSecrets).isNotNull();
        assertThat(typedSecrets.size()).isEqualTo(1);
        final TypedSecret typedSecret = typedSecrets.get(0);
        assertThat(typedSecret.getSecret()).isEqualTo(HexUtils.toHexString("secret".getBytes()));
        assertThat(typedSecret.getSecretType()).isEqualTo(SecretType.E_METER_MASTER_KEY);
    }

    @Test
    public void retrieveSecrets_decryptionError() throws Exception {
        //GIVEN
        final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
        final DbEncryptedSecret secret = new DbEncryptedSecret();
        secret.setSecretType(SecretType.E_METER_MASTER_KEY);
        secret.setEncryptionKeyReference(keyReference);

        //WHEN
        when(this.secretRepository.findValidOrderedByKeyValidFrom(anyString(), any(), any(), any(), any())).thenReturn(
                new PageImpl<>(Arrays.asList(secret)));
        when(this.encryptionDelegate.decrypt(any(), any())).thenThrow(new RuntimeException("Decryption error"));

        //THEN
        assertThatIllegalStateException().isThrownBy(
                () -> this.service.retrieveSecrets("SOME_DEVICE", Arrays.asList(SecretType.E_METER_MASTER_KEY)));
    }

    @Test
    public void retrieveSecrets_secretWithoutKey() throws Exception {
        final DbEncryptedSecret secret = new DbEncryptedSecret();
        when(this.secretRepository.findValidOrderedByKeyValidFrom(anyString(), any(), any(), any(), any())).thenReturn(
                new PageImpl<>(Arrays.asList(secret)));
        assertThatIllegalStateException().isThrownBy(
                () -> this.service.retrieveSecrets("SOME_DEVICE", Arrays.asList(SecretType.E_METER_MASTER_KEY)));
    }

    @Test
    public void retrieveSecrets_noSecrets() throws Exception {
        when(this.secretRepository.findValidOrderedByKeyValidFrom(anyString(), any(), any(), any(), any())).thenReturn(
                Page.empty());
        assertThatIllegalStateException().isThrownBy(
                () -> this.service.retrieveSecrets("SOME_DEVICE", Arrays.asList(SecretType.E_METER_MASTER_KEY)));
    }

    @Test
    public void retrieveSecrets_multipleSecrets() throws Exception {
        when(this.secretRepository.findValidOrderedByKeyValidFrom(anyString(), any(), any(), any(), any())).thenReturn(
                new PageImpl<>(Arrays.asList(new DbEncryptedSecret(), new DbEncryptedSecret())));
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

        //WHEN
        when(this.keyRepository.findByTypeAndValid(any(), any(), any())).thenReturn(
                new PageImpl<>(Arrays.asList(keyReference)));
        when(this.encryptionDelegate.encrypt(any(), any(), anyString())).thenReturn(encryptedSecret);
        this.service.storeSecrets("SOME_DEVICE", Arrays.asList(typedSecret));
    }

    @Test
    public void storeSecrets_secretNull() throws Exception {
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
    public void storeSecrets_secretTypeNull() throws Exception {
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
    public void storeSecrets_noKey() throws Exception {
        //GIVEN
        final TypedSecret typedSecret = new TypedSecret();
        typedSecret.setSecretType(SecretType.E_METER_MASTER_KEY);
        typedSecret.setSecret(HexUtils.toHexString("$3cr3t".getBytes()));
        //WHEN
        when(this.keyRepository.findByTypeAndValid(any(), any(), any())).thenReturn(Page.empty());
        //THEN
        assertThatIllegalStateException().isThrownBy(
                () -> this.service.storeSecrets("SOME_DEVICE", Arrays.asList(typedSecret)));

    }

    @Test
    public void storeSecrets_multipleKeys() throws Exception {
        //GIVEN
        final TypedSecret typedSecret = new TypedSecret();
        typedSecret.setSecretType(SecretType.E_METER_MASTER_KEY);
        typedSecret.setSecret(HexUtils.toHexString("$3cr3t".getBytes()));
        //WHEN
        when(this.keyRepository.findByTypeAndValid(any(), any(), any())).thenReturn(
                new PageImpl<>(Arrays.asList(new DbEncryptionKeyReference(), new DbEncryptionKeyReference())));
        //THEN
        assertThatIllegalStateException().isThrownBy(
                () -> this.service.storeSecrets("SOME_DEVICE", Arrays.asList(typedSecret)));

    }

    @Test
    public void storeSecrets_encryptionError() throws Exception {
        //GIVEN
        final TypedSecret typedSecret = new TypedSecret();
        typedSecret.setSecretType(SecretType.E_METER_MASTER_KEY);
        typedSecret.setSecret(HexUtils.toHexString("$3cr3t".getBytes()));
        final DbEncryptionKeyReference keyReference = new DbEncryptionKeyReference();
        keyReference.setEncryptionProviderType(EncryptionProviderType.JRE);
        keyReference.setReference("keyReferenceString");

        //WHEN
        when(this.keyRepository.findByTypeAndValid(any(), any(), any())).thenReturn(
                new PageImpl<>(Arrays.asList(keyReference)));
        when(this.encryptionDelegate.encrypt(any(), any(), anyString())).thenThrow(
                new RuntimeException("Encryption error"));
        //THEN
        assertThatIllegalStateException().isThrownBy(
                () -> this.service.storeSecrets("SOME_DEVICE", Arrays.asList(typedSecret)));

    }

}
