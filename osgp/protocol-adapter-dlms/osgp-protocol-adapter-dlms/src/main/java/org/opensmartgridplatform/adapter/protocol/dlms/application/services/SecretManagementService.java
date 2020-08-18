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
import org.apache.commons.lang3.NotImplementedException;
import org.opensmartgridplatform.adapter.protocol.dlms.application.wsclient.SecretManagementClient;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.security.EncryptedSecret;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;
import org.opensmartgridplatform.shared.security.Secret;
import org.opensmartgridplatform.shared.security.providers.RsaEncryptionProvider;
import org.opensmartgridplatform.ws.schema.core.secret.management.GetSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.GetSecretsResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.SecretType;
import org.opensmartgridplatform.ws.schema.core.secret.management.SecretTypes;
import org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecret;
import org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecrets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SecretManagementService implements SecurityKeyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecretManagementService.class);
    private final RsaEncryptionProvider rsaEncryptionProvider;
    private final SecretManagementClient secretManagementClient;

    public SecretManagementService(RsaEncryptionProvider rsaEncryptionProvider,
            SecretManagementClient secretManagementClient) {
        this.rsaEncryptionProvider = rsaEncryptionProvider;
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
    public byte[] reEncryptKey(final byte[] externallyEncryptedKey, final SecurityKeyType keyType)
            throws FunctionalException {
        // TODO:
        /*
        if (externallyEncryptedKey == null) {
            return new byte[0];
        }

        final byte[] key = this.rsaDecrypt(externallyEncryptedKey, keyType);
        return this.aesEncrypt(key, keyType);

         */
        throw new NotImplementedException();
    }

    private byte[] rsaDecrypt(final byte[] externallyEncryptedKey, final SecurityKeyType keyType)
            throws FunctionalException {
        // TODO:
        /*
        try {
            return this.rsaEncryptionService.decrypt(externallyEncryptedKey);
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during decryption", e);

            throw new FunctionalException(FunctionalExceptionType.DECRYPTION_EXCEPTION, ComponentType.PROTOCOL_DLMS,
                    new EncrypterException(
                            String.format("Unexpected exception during decryption of %s key.", keyType)));
        }*/
        throw new NotImplementedException();
    }

    private byte[] aesEncrypt(final byte[] key, final SecurityKeyType keyType) throws FunctionalException {
        // TODO:
        /*
        try {
            return this.encryptionService.encrypt(key);
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during encryption", e);

            throw new FunctionalException(FunctionalExceptionType.ENCRYPTION_EXCEPTION, ComponentType.PROTOCOL_DLMS,
                    new EncrypterException(
                            String.format("Unexpected exception during encryption of %s key.", keyType)));
        }*/
        throw new NotImplementedException();
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
    public byte[] decryptKey(final byte[] encryptedKey, final SecurityKeyType keyType) throws ProtocolAdapterException {
        //TODO:
        /*
        if (encryptedKey == null) {
            return new byte[0];
        }
        try {
            return this.encryptionService.decrypt(encryptedKey);
        } catch (final Exception e) {
            throw new ProtocolAdapterException("Error decrypting " + keyType + " key", e);
        }*/
        throw new NotImplementedException();
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
        //TODO:
        /*
        if (plainKey == null) {
            return new byte[0];
        }
        try {
            return this.encryptionService.encrypt(plainKey);
        } catch (final Exception e) {
            throw new ProtocolAdapterException("Error encrypting " + keyType + " key", e);
        }*/
        throw new NotImplementedException();
    }

    @Override
    public byte[] getDlmsMasterKey(String deviceIdentification) {
        LOGGER.info("Retrieving DLMS master key for device {}", deviceIdentification);
        return getSecret(deviceIdentification, SecretType.E_METER_MASTER_KEY);
    }

    @Override
    public byte[] getDlmsAuthenticationKey(String deviceIdentification) {
        LOGGER.info("Retrieving DLMS authentication key for device {}", deviceIdentification);
        return getSecret(deviceIdentification, SecretType.E_METER_AUTHENTICATION_KEY);
    }

    @Override
    public byte[] getDlmsGlobalUnicastEncryptionKey(String deviceIdentification) {
        LOGGER.info("Retrieving DLMS global unicast encryption key for device {}", deviceIdentification);
        return getSecret(deviceIdentification, SecretType.E_METER_ENCRYPTION_KEY_UNICAST);
    }

    @Override
    public byte[] getMbusDefaultKey(String mbusDeviceIdentification) {
        LOGGER.info("Retrieving M-Bus Default key for device {}", mbusDeviceIdentification);
        return getSecret(mbusDeviceIdentification, SecretType.G_METER_MASTER_KEY);
    }

    @Override
    public byte[] getMbusUserKey(String mbusDeviceIdentification) {
        LOGGER.info("Retrieving M-Bus User key for device {}", mbusDeviceIdentification);
        return getSecret(mbusDeviceIdentification, SecretType.G_METER_ENCRYPTION_KEY);
    }

    @Override
    public byte[] getDlmsPassword(String deviceIdentification) {
        LOGGER.info("Retrieving DLMS LLS Password for device {}", deviceIdentification);
        return getSecret(deviceIdentification, SecretType.PPP_PASSWORD);
    }

    @Override
    public DlmsDevice storeNewKey(DlmsDevice device, byte[] encryptedKey, SecurityKeyType keyType) {
        throw new NotImplementedException();
    }

    @Override
    public DlmsDevice validateNewKey(DlmsDevice device, SecurityKeyType keyType) throws ProtocolAdapterException {
        throw new NotImplementedException();
    }

    @Override
    public byte[] generateKey() {
        throw new NotImplementedException();
    }

    @Override
    public byte[] generateAndEncryptKey() {
        throw new NotImplementedException();
    }

    private byte[] getSecret(String deviceIdentification, SecretType secretType) {
        try {
            GetSecretsRequest request = getSoapRequestForKey(deviceIdentification, secretType);
            GetSecretsResponse response = secretManagementClient.getSecretsRequest(request);
            Optional<TypedSecret> optionalTypedSecret = getTypedSecretFromSoapResponse(response, secretType);

            byte[] decryptedKey = decryptSoapSecret(deviceIdentification, optionalTypedSecret.orElseThrow(
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

    private GetSecretsRequest getSoapRequestForKey(String deviceIdentification, SecretType secretType) {
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
            EncryptedSecret encryptedSoapSecret = new EncryptedSecret(EncryptionProviderType.RSA,
                    encryptedDecodedSoapSecret);
            Secret decryptedSecret = rsaEncryptionProvider.decrypt(encryptedSoapSecret, "1");
            return decryptedSecret.getSecret();
        } catch (Exception e) {
            throw new IllegalStateException("Decrypting key for device: " + deviceIdentification, e);
        }
    }
}
