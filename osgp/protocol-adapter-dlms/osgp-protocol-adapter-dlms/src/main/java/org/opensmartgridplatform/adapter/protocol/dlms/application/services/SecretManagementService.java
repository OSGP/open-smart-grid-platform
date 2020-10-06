/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.opensmartgridplatform.adapter.protocol.dlms.application.wsclient.SecretManagementClient;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.security.EncryptionService;
import org.opensmartgridplatform.shared.security.RsaEncrypter;
import org.opensmartgridplatform.shared.security.RsaEncryptionService;
import org.opensmartgridplatform.ws.schema.core.secret.management.ActivateSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.GenerateAndStoreSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.GenerateAndStoreSecretsResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.GetSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.GetSecretsResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.HasNewSecretRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.HasNewSecretResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.SecretType;
import org.opensmartgridplatform.ws.schema.core.secret.management.SecretTypes;
import org.opensmartgridplatform.ws.schema.core.secret.management.StoreSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecret;
import org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecrets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SecretManagementService implements SecurityKeyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecretManagementService.class);
    private final RsaEncrypter soapSecretsRsaEncryptionProvider;
    private final SecretManagementClient secretManagementClient;

    @Autowired
    private RsaEncryptionService rsaEncryptionService;

    @Autowired
    private EncryptionService aesEncryptionService;

    public SecretManagementService(RsaEncrypter soapSecretsRsaEncryptionProvider,
            SecretManagementClient secretManagementClient) {
        this.soapSecretsRsaEncryptionProvider = soapSecretsRsaEncryptionProvider;
        this.secretManagementClient = secretManagementClient;
    }

    /**
     * Re-encrypts the given key with a secret known only inside this protocol
     * adapter.
     * <p>
     * New keys can be provided to OSGP from outside in a form encrypted with
     * the public key from an asymmetrical key pair for the platform, which is
     * available to external organizations.<br>
     * Inside the DLMS protocol adapter keys are encrypted with a faster
     * symmetrical encryption using a secret key that is not supposed to be
     * known outside this protocol adapter.
     *
     * @param externallyEncryptedKey
     *         key encrypted with the externally known public key for OSGP
     * @param keyType
     *         type of the key, for logging purposes
     *
     * @return the key encrypted with the symmetrical secret key used only
     *         inside the DLMS protocol adapter, or an empty byte array if
     *         {@code externallyEncryptedKey == null}
     *
     * @throws FunctionalException
     *         in case of a encryption/decryption errors while handling the
     *         key
     */
    @Override
    public byte[] reEncryptKey(final byte[] externallyEncryptedKey, final SecurityKeyType keyType)
            throws FunctionalException {

        if (externallyEncryptedKey == null) {
            return new byte[0];
        }

        final byte[] key = this.rsaDecrypt(externallyEncryptedKey, keyType);
        return this.aesEncryptKey(key, keyType);

    }

    @Override
    public byte[] rsaDecrypt(final byte[] externallyEncryptedKey, final SecurityKeyType keyType)
            throws FunctionalException {

        try {
            return this.rsaEncryptionService.decrypt(externallyEncryptedKey);
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during decryption", e);

            throw new FunctionalException(FunctionalExceptionType.DECRYPTION_EXCEPTION, ComponentType.PROTOCOL_DLMS,
                    new EncrypterException(
                            String.format("Unexpected exception during decryption of %s key.", keyType)));
        }

    }

    @Override
    public byte[] aesEncryptKey(final byte[] key, final SecurityKeyType keyType) throws FunctionalException {

        try {
            return this.aesEncryptionService.encrypt(key);
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during encryption", e);

            throw new FunctionalException(FunctionalExceptionType.ENCRYPTION_EXCEPTION, ComponentType.PROTOCOL_DLMS,
                    new EncrypterException(
                            String.format("Unexpected exception during encryption of %s key.", keyType)));
        }
    }

    /**
     * Decrypts the given symmetrically encrypted key.
     * <p>
     * <strong>NB:</strong> Only decrypt keys like this at the moment they are
     * required as part of the communication with a device.
     *
     * @param encryptedKey
     *         key encrypted with the symmetrical key internal to the DLMS
     *         protocol adapter.
     * @param keyType
     *         type of the key, for logging purposes
     *
     * @return the plain key, or an empty byte array if
     *         {@code encryptedKey == null}
     */
    @Override
    public byte[] aesDecryptKey(final byte[] encryptedKey, final SecurityKeyType keyType) throws FunctionalException {

        if (encryptedKey == null) {
            return new byte[0];
        }
        try {
            return this.aesEncryptionService.decrypt(encryptedKey);
        } catch (final Exception e) {
            throw new FunctionalException(FunctionalExceptionType.ENCRYPTION_EXCEPTION, ComponentType.SHARED, e);
        }
    }

    /**
     * Encrypts the given {@code plainKey} with the symmetrical secret key that
     * is internal to the DLMS protocol adapter.
     *
     * @param plainKey
     *            plain key without encryption
     * @param keyType
     *            type of the key, for logging purposes
     * @return the given key encrypted with the symmetrical key internal to the
     *         DLMS protocol adapter.
     */
    public byte[] encryptKey(final byte[] plainKey, final SecurityKeyType keyType) throws ProtocolAdapterException {

        if (plainKey == null) {
            return new byte[0];
        }
        try {
            return this.aesEncryptionService.encrypt(plainKey);
        } catch (final Exception e) {
            throw new ProtocolAdapterException("Error encrypting " + keyType + " key", e);
        }
    }

    @Override
    public byte[] getDlmsMasterKey(String deviceIdentification) {
        LOGGER.info("Retrieving DLMS master key for device {}", deviceIdentification);
        return this.getSecret(deviceIdentification, SecretType.E_METER_MASTER_KEY);
    }

    @Override
    public byte[] getDlmsAuthenticationKey(String deviceIdentification) {
        LOGGER.info("Retrieving DLMS authentication key for device {}", deviceIdentification);
        return this.getSecret(deviceIdentification, SecretType.E_METER_AUTHENTICATION_KEY);
    }

    @Override
    public byte[] getDlmsGlobalUnicastEncryptionKey(String deviceIdentification) {
        LOGGER.info("Retrieving DLMS global unicast encryption key for device {}", deviceIdentification);
        return this.getSecret(deviceIdentification, SecretType.E_METER_ENCRYPTION_KEY_UNICAST);
    }

    @Override
    public byte[] getMbusDefaultKey(String mbusDeviceIdentification) {
        LOGGER.info("Retrieving M-Bus Default key for device {}", mbusDeviceIdentification);
        return this.getSecret(mbusDeviceIdentification, SecretType.G_METER_MASTER_KEY);
    }

    @Override
    public byte[] getMbusUserKey(String mbusDeviceIdentification) {
        LOGGER.info("Retrieving M-Bus User key for device {}", mbusDeviceIdentification);
        return this.getSecret(mbusDeviceIdentification, SecretType.G_METER_ENCRYPTION_KEY);
    }

    @Override
    public byte[] getDlmsPassword(String deviceIdentification) {
        LOGGER.info("Retrieving DLMS LLS Password for device {}", deviceIdentification);
        return this.getSecret(deviceIdentification, SecretType.PPP_PASSWORD);
    }

    @Override
    public byte[][] getKeys(String deviceIdentification, SecurityKeyType[] keyTypes) {
        GetSecretsRequest request = new GetSecretsRequest();
        request.setDeviceId(deviceIdentification);
        request.setSecretTypes(new SecretTypes());
        List<SecretType> secretTypeList = request.getSecretTypes().getSecretType();

        for (SecurityKeyType keyType: keyTypes) {
            secretTypeList.add(this.getSecretTypeFrom(keyType));
        }

        GetSecretsResponse response = this.secretManagementClient.getSecretsRequest(request);

        TypedSecrets typedSecrets = response.getTypedSecrets();
        List<TypedSecret> typedSecretList = typedSecrets.getTypedSecret();

        if (keyTypes.length != typedSecretList.size()) {
            throw new IllegalStateException("Unexpected number of secrets in response");
        }

        byte[][] decryptedKeys = new byte[keyTypes.length][];

        for (int i = 0; i < keyTypes.length; i++) {
            if (typedSecretList.get(i).getType().equals(this.getSecretTypeFrom(keyTypes[i]))) {
                decryptedKeys[i] = this.decryptSoapSecret(deviceIdentification, typedSecretList.get(i));
            }
        }

        return decryptedKeys;
    }

    @Override
    public void aesDecryptAndStoreNewKey(String deviceIdentification, SecurityKeyType keyType, byte[]encryptedKey) throws FunctionalException {
        byte[] plainKey = this.aesDecryptKey(encryptedKey, keyType);
        this.storeNewKeys(deviceIdentification, new  SecurityKeyType[] { keyType}, new byte[][] {plainKey});
    }

    @Override
    public void storeNewKey(String deviceIdentification, SecurityKeyType keyType, byte[]key) {
        this.storeNewKeys(deviceIdentification, new  SecurityKeyType[] { keyType}, new byte[][] {key});
    }

    @Override
    public void storeNewKeys(String deviceIdentification, SecurityKeyType[] keyTypes, byte[][]keys) {
        StoreSecretsRequest request = new StoreSecretsRequest();
        request.setDeviceId(deviceIdentification);

        TypedSecrets typedSecrets = new TypedSecrets();
        request.setTypedSecrets(new TypedSecrets());
        List<TypedSecret> typedSecretList = typedSecrets.getTypedSecret();

        for (int i = 0; i < keyTypes.length; i++) {
            TypedSecret typedSecret = new TypedSecret();
            typedSecret.setSecret(this.encryptSoapSecret(deviceIdentification, keys[i]));
            typedSecret.setType(this.getSecretTypeFrom(keyTypes[i]));
            typedSecretList.add(typedSecret);
        }

        this.secretManagementClient.storeSecretsRequest(request);
    }

    @Override
    public void activateNewKey(String deviceIdentification, SecurityKeyType keyType) throws ProtocolAdapterException {
        ActivateSecretsRequest request = new ActivateSecretsRequest();
        request.setDeviceId(deviceIdentification);
        request.setSecretTypes(new SecretTypes());
        List<SecretType> secretTypeList = request.getSecretTypes().getSecretType();
        secretTypeList.add(this.getSecretTypeFrom(keyType));
        this.secretManagementClient.activateSecretsRequest(request);
    }

    @Override
    public boolean isActivated(String deviceIdentification, SecurityKeyType keyType) {
        //isActivated is = !hasNewSecret
        HasNewSecretRequest request = new HasNewSecretRequest();
        request.setDeviceId(deviceIdentification);
        request.setSecretType(this.getSecretTypeFrom(keyType));
        HasNewSecretResponse response = this.secretManagementClient.hasNewSecretRequest(request);
        return !response.isHasNewSecret();
    }

    /**
     * Generates a new key that can be used as DLMS master key, authentication
     * key, global unicast encryption key, M-Bus Default key or M-Bus User key.
     * <p>
     * The master keys (DLMS master or M-Bus Default) cannot be changed on a
     * device, but can be generated for use in tests or with simulated devices.
     *
     * @return a new 128bits AES key.
     */
    @Override
    public byte[][] generateAES128BitsKeysAndStoreAsNewKeys(String deviceIdentification, SecurityKeyType[] keyTypes) {
        GenerateAndStoreSecretsRequest request = new GenerateAndStoreSecretsRequest();

        request.setDeviceId(deviceIdentification);
        request.setSecretTypes(new SecretTypes());
        List<SecretType> secretTypeList = request.getSecretTypes().getSecretType();

        for (SecurityKeyType keyType: keyTypes) {
            secretTypeList.add(this.getSecretTypeFrom(keyType));
        }

        GenerateAndStoreSecretsResponse response = this.secretManagementClient.generateAndStoreSecrets(request);

        TypedSecrets typedSecrets = response.getTypedSecrets();
        List<TypedSecret> typedSecretList = typedSecrets.getTypedSecret();

        if (keyTypes.length != typedSecretList.size()) {
            throw new IllegalStateException("Unexpected number of secrets in response");
        }

        byte[][] decryptedKeys = new byte[keyTypes.length][];

        for (int i = 0; i < keyTypes.length; i++) {
            if (typedSecretList.get(i).getType().equals(this.getSecretTypeFrom(keyTypes[i]))) {
                decryptedKeys[i] = this.decryptSoapSecret(deviceIdentification, typedSecretList.get(i));
            }
        }

        return decryptedKeys;
    }

    private SecretType getSecretTypeFrom(SecurityKeyType keyType) {
        switch (keyType) {
            case E_METER_AUTHENTICATION: return SecretType.E_METER_AUTHENTICATION_KEY;
            case E_METER_MASTER: return SecretType.E_METER_MASTER_KEY;
            case E_METER_ENCRYPTION: return SecretType.E_METER_ENCRYPTION_KEY_UNICAST;
            case G_METER_MASTER: return SecretType.G_METER_MASTER_KEY;
            default: throw new IllegalStateException("Invalid SecurityKeyType specified");
        }
    }

    private byte[] getSecret(String deviceIdentification, SecretType secretType) {
        try {
            GetSecretsRequest request = this.getSoapGetRequestForSingleKey(deviceIdentification, secretType);
            GetSecretsResponse response = this.secretManagementClient.getSecretsRequest(request);
            Optional<TypedSecret> optionalTypedSecret = this.getTypedSecretFromSoapResponse(response, secretType);

            byte[] decryptedKey = this.decryptSoapSecret(deviceIdentification, optionalTypedSecret.orElseThrow(
                    () -> new IllegalStateException("Secret not found:" + deviceIdentification + " " + secretType.name())));

            log.trace(secretType.name() + " for device " + deviceIdentification + " is " + Hex.encodeHexString(decryptedKey));

            return decryptedKey;

        } catch (Exception e) {
            throw new EncrypterException("Error while retrieving encryption key", e);
        }
    }

    private Optional<TypedSecret> getTypedSecretFromSoapResponse(GetSecretsResponse response, SecretType secretType) {
        TypedSecrets typedSecrets = response.getTypedSecrets();
        List<TypedSecret> typedSecretList = typedSecrets.getTypedSecret();

        return typedSecretList.stream().filter(typedSecret -> typedSecret.getType() == secretType).findFirst();
    }

    private GetSecretsRequest getSoapGetRequestForSingleKey(String deviceIdentification, SecretType secretType) {
        GetSecretsRequest request = new GetSecretsRequest();
        request.setDeviceId(deviceIdentification);
        request.setSecretTypes(new SecretTypes());
        List<SecretType> secretTypeList = request.getSecretTypes().getSecretType();
        secretTypeList.add(secretType);
        return request;
    }

    private byte[] decryptSoapSecret(String deviceIdentification, TypedSecret typedSecret) {
        try {
            byte[] encryptedDecodedSoapSecret = Hex.decodeHex(typedSecret.getSecret());
            return this.soapSecretsRsaEncryptionProvider.decrypt(encryptedDecodedSoapSecret);
        } catch (Exception e) {
            throw new IllegalStateException("Decrypting key for device: " + deviceIdentification, e);
        }
    }

    private String encryptSoapSecret(String deviceIdentification, byte[] secret) {
        try {
            byte[] encrypted = this.soapSecretsRsaEncryptionProvider.encrypt(secret);
            return Hex.encodeHexString(encrypted);
        } catch (Exception e) {
            throw new IllegalStateException("Encrypting key for device: " + deviceIdentification, e);
        }
    }
}
