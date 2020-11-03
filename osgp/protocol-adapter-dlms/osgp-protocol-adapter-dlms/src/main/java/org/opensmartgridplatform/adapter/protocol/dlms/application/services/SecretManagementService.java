/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.opensmartgridplatform.adapter.protocol.dlms.application.wsclient.SecretManagementClient;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.security.EncryptionService;
import org.opensmartgridplatform.shared.security.RsaEncrypter;
import org.opensmartgridplatform.shared.security.RsaEncryptionService;
import org.opensmartgridplatform.ws.schema.core.secret.management.ActivateSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.GenerateAndStoreSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.GenerateAndStoreSecretsResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.GetNewSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.GetNewSecretsResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.GetSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.GetSecretsResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.HasNewSecretRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.HasNewSecretResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.OsgpResultType;
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
    private final RsaEncrypter soapRsaEncrypter;
    private final SecretManagementClient secretManagementClient;

    @Autowired
    private RsaEncryptionService rsaEncryptionService;

    @Autowired
    private EncryptionService aesEncryptionService;

    public SecretManagementService(RsaEncrypter soapRsaEncrypter, SecretManagementClient secretManagementClient) {
        this.soapRsaEncrypter = soapRsaEncrypter;
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
     *
     * @return the key encrypted with the symmetrical secret key used only
     *         inside the DLMS protocol adapter, or an empty byte array if
     *         {@code externallyEncryptedKey == null}
     *
     * @throws FunctionalException
     *         in case of a encryption/decryption errors while handling the
     *         key
     */
    //@Override
    /*public byte[] reEncryptKey(final byte[] externallyEncryptedKey) throws FunctionalException {

        if (externallyEncryptedKey == null) {
            return new byte[0];
        }

        final byte[] key = this.rsaDecrypt(externallyEncryptedKey);
        return this.aesEncryptKey(key);

    }*/

    @Override
    public byte[] rsaDecrypt(final byte[] externallyEncryptedKey) throws FunctionalException {
        try {
            return this.rsaEncryptionService.decrypt(externallyEncryptedKey);
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during decryption", e);
            throw new FunctionalException(FunctionalExceptionType.DECRYPTION_EXCEPTION, ComponentType.PROTOCOL_DLMS, e);
        }
    }

    //@Override
    /*public byte[] aesEncryptKey(final byte[] key) throws FunctionalException {
        try {
            return this.aesEncryptionService.encrypt(key);
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during encryption", e);
            throw new FunctionalException(FunctionalExceptionType.ENCRYPTION_EXCEPTION, ComponentType.PROTOCOL_DLMS, e);
        }
    }*/

    /*
     * Decrypts the given symmetrically encrypted key.
     * <p>
     * <strong>NB:</strong> Only decrypt keys like this at the moment they are
     * required as part of the communication with a device.
     *
     * @param encryptedKey
     *         key encrypted with the symmetrical key internal to the DLMS
     *         protocol adapter.
     *
     * @return the plain key, or an empty byte array if
     *         {@code encryptedKey == null}
     */
    //@Override
    /*public byte[] aesDecryptKey(final byte[] encryptedKey) throws FunctionalException {
        if (encryptedKey == null) {
            throw new IllegalArgumentException("Cannot decrypt NULL key");
        }
        try {
            return this.aesEncryptionService.decrypt(encryptedKey);
        } catch (final Exception e) {
            throw new FunctionalException(FunctionalExceptionType.ENCRYPTION_EXCEPTION, ComponentType.PROTOCOL_DLMS, e);
        }
    }*/

    @Override
    public byte[] getKey(String deviceIdentification, SecurityKeyType keyType) {
        LOGGER.info("Retrieving {} for device {}", keyType.name(), deviceIdentification);
        return this.getKeys(deviceIdentification, Arrays.asList(keyType)).get(keyType);
    }

    @Override
    public Map<SecurityKeyType, byte[]> getKeys(String deviceIdentification, List<SecurityKeyType> keyTypes) {
        GetSecretsRequest request = this.createGetSecretsRequest(deviceIdentification, keyTypes);
        GetSecretsResponse response = this.secretManagementClient.getSecretsRequest(request);
        this.validateGetResponse(keyTypes, response);
        return this.convertSoapSecretsToSecretMapByType(response.getTypedSecrets().getTypedSecret());
    }

    @Override
    public byte[] getNewKey(String deviceIdentification, SecurityKeyType keyType) {
        LOGGER.info("Retrieving new {} for device {}", keyType.name(), deviceIdentification);
        return this.getNewKeys(deviceIdentification, Arrays.asList(keyType)).get(keyType);
    }

    @Override
    public Map<SecurityKeyType, byte[]> getNewKeys(String deviceIdentification, List<SecurityKeyType> keyTypes) {
        GetNewSecretsRequest request = this.createGetNewSecretsRequest(deviceIdentification, keyTypes);
        GetNewSecretsResponse response = this.secretManagementClient.getNewSecretsRequest(request);
        this.validateGetNewResponse(keyTypes, response);
        return this.convertSoapSecretsToSecretMapByType(response.getTypedSecrets().getTypedSecret());
    }

    private void validateGetResponse(List<SecurityKeyType> keyTypes, GetSecretsResponse response) {
        this.validateOsgpResultAndTypedSecrets(response.getResult(), response.getTechnicalFault(),
                response.getTypedSecrets(), keyTypes.size());
    }

    private void validateGetNewResponse(List<SecurityKeyType> keyTypes, GetNewSecretsResponse response) {
        this.validateOsgpResultAndTypedSecrets(response.getResult(), response.getTechnicalFault(),
                response.getTypedSecrets(), keyTypes.size());
    }

    private Map<SecurityKeyType, byte[]> convertSoapSecretsToSecretMapByType(List<TypedSecret> soapSecrets) {
        Function<TypedSecret, SecurityKeyType> convertType = ts -> SecurityKeyType.fromSecretType(ts.getType());
        Function<TypedSecret, byte[]> convertSecret = ts -> this.decryptSoapSecret(ts);
        return soapSecrets.stream().collect(Collectors.toMap(convertType, convertSecret));
    }

    private GetSecretsRequest createGetSecretsRequest(String deviceIdentification, List<SecurityKeyType> keyTypes) {
        GetSecretsRequest request = new GetSecretsRequest();
        request.setDeviceId(deviceIdentification);
        request.setSecretTypes(new SecretTypes());
        List<SecretType> secretTypeList = request.getSecretTypes().getSecretType();
        keyTypes.stream().forEach(kt -> secretTypeList.add(kt.toSecretType()));
        return request;
    }

    private GetNewSecretsRequest createGetNewSecretsRequest(String deviceIdentification,
            List<SecurityKeyType> keyTypes) {
        GetNewSecretsRequest request = new GetNewSecretsRequest();
        request.setDeviceId(deviceIdentification);
        request.setSecretTypes(new SecretTypes());
        List<SecretType> secretTypeList = request.getSecretTypes().getSecretType();
        keyTypes.stream().forEach(kt -> secretTypeList.add(kt.toSecretType()));
        return request;
    }

    /*@Override
    public void aesDecryptAndStoreNewKey(String deviceIdentification, SecurityKeyType keyType, byte[] encryptedKey)
            throws FunctionalException {
        byte[] plainKey = this.aesDecryptKey(encryptedKey); //, keyType);
        this.storeNewKeys(deviceIdentification, new SecurityKeyType[] { keyType }, new byte[][] { plainKey });
    }*/

    @Override
    public void storeNewKey(String deviceIdentification, SecurityKeyType keyType, byte[] key) {
        Map<SecurityKeyType, byte[]> keysByType = new HashMap<>();
        keysByType.put(keyType, key);
        this.storeNewKeys(deviceIdentification, keysByType);
    }

    @Override
    public void storeNewKeys(String deviceIdentification, Map<SecurityKeyType, byte[]> keysByType) {
        TypedSecrets typedSecrets = new TypedSecrets();
        List<TypedSecret> typedSecretList = typedSecrets.getTypedSecret();
        for (SecurityKeyType type : keysByType.keySet()) {
            TypedSecret ts = new TypedSecret();
            ts.setType(type.toSecretType());
            ts.setSecret(this.encryptSoapSecret(keysByType.get(type)));
            typedSecretList.add(ts);
        }
        StoreSecretsRequest request = this.createStoreSecretsRequest(deviceIdentification, typedSecrets);
        this.secretManagementClient.storeSecretsRequest(request);
    }

    private StoreSecretsRequest createStoreSecretsRequest(String deviceIdentification, TypedSecrets typedSecrets) {
        StoreSecretsRequest request = new StoreSecretsRequest();
        request.setDeviceId(deviceIdentification);
        request.setTypedSecrets(typedSecrets);
        return request;
    }

    @Override
    public void activateNewKey(String deviceIdentification, SecurityKeyType keyType) throws ProtocolAdapterException {
        this.activateNewKeys(deviceIdentification, Arrays.asList(keyType));
    }

    @Override
    public void activateNewKeys(String deviceIdentification, List<SecurityKeyType> keyTypes)
            throws ProtocolAdapterException {
        ActivateSecretsRequest request = new ActivateSecretsRequest();
        request.setDeviceId(deviceIdentification);
        request.setSecretTypes(new SecretTypes());
        List<SecretType> secretTypeList = request.getSecretTypes().getSecretType();
        keyTypes.forEach(kt -> secretTypeList.add(kt.toSecretType()));
        this.secretManagementClient.activateSecretsRequest(request);
    }

    @Override
    public boolean hasNewSecretOfType(String deviceIdentification, SecurityKeyType keyType) {
        HasNewSecretRequest request = new HasNewSecretRequest();
        request.setDeviceId(deviceIdentification);
        request.setSecretType(keyType.toSecretType());
        HasNewSecretResponse response = this.secretManagementClient.hasNewSecretRequest(request);
        return response.isHasNewSecret();
    }

    @Override
    public byte[] generate128BitsKeyAndStoreAsNewKey(String deviceIdentification, SecurityKeyType keyType) {
        return this.generate128BitsKeysAndStoreAsNewKeys(deviceIdentification, Arrays.asList(keyType)).get(keyType);
    }

    /**
     * Generates a new key that can be used as DLMS master key, authentication
     * key, global unicast encryption key, M-Bus Default key or M-Bus User key.
     * <p>
     * The master keys (DLMS master or M-Bus Default) cannot be changed on a
     * device, but can be generated for use in tests or with simulated devices.
     *
     * @return a new 128bits key, unencrypted.
     */
    @Override
    public Map<SecurityKeyType, byte[]> generate128BitsKeysAndStoreAsNewKeys(String deviceIdentification,
            List<SecurityKeyType> keyTypes) {
        SecretTypes secretTypes = new SecretTypes();
        GenerateAndStoreSecretsRequest request = this
                .createGenerateAndStoreSecretsRequest(deviceIdentification, secretTypes);
        secretTypes.getSecretType().addAll(keyTypes.stream().map(SecurityKeyType::toSecretType).collect(toList()));

        GenerateAndStoreSecretsResponse response = this.secretManagementClient.generateAndStoreSecrets(request);
        TypedSecrets typedSecrets = response.getTypedSecrets();
        List<TypedSecret> typedSecretList = typedSecrets.getTypedSecret();
        this.validateGenerateAndStoreResponse(keyTypes, response, typedSecretList);
        return this.convertSoapSecretsToSecretMapByType(typedSecrets.getTypedSecret());
    }

    private GenerateAndStoreSecretsRequest createGenerateAndStoreSecretsRequest(String deviceIdentification,
            SecretTypes secretTypes) {
        GenerateAndStoreSecretsRequest request = new GenerateAndStoreSecretsRequest();
        request.setDeviceId(deviceIdentification);
        request.setSecretTypes(secretTypes);
        return request;
    }

    private void validateGenerateAndStoreResponse(List<SecurityKeyType> keyTypes,
            GenerateAndStoreSecretsResponse response, List<TypedSecret> typedSecretList) {
        this.validateOsgpResultAndTypedSecrets(response.getResult(), response.getTechnicalFault(),
                response.getTypedSecrets(), keyTypes.size());
        typedSecretList.forEach(ts-> {
            if(ts.getSecret()==null) {
                throw new IllegalStateException(String.format("Generated a NULL key (key_type=%s)",
                        ts.getType()));
            }
        });
    }

    private void validateOsgpResultAndTypedSecrets(OsgpResultType result, Object fault, TypedSecrets typedSecrets,
            int expectedNrKeys) {
        if (!OsgpResultType.OK.equals(result)) {
            throw new IllegalStateException("Could not generate and store keys in secret-mgmt: " + fault.toString());
        } else if (typedSecrets == null || typedSecrets.getTypedSecret() == null) {
            throw new IllegalStateException("No secrets in response");
        } else if (expectedNrKeys != typedSecrets.getTypedSecret().size()) {
            throw new IllegalStateException(
                    String.format("Unexpected number of secrets in response: expected %s but " + "found %s",
                            expectedNrKeys, typedSecrets.getTypedSecret().size()));
        }
    }

    private byte[] decryptSoapSecret(TypedSecret typedSecret) {
        try {
            byte[] encryptedDecodedSoapSecret = Hex.decodeHex(typedSecret.getSecret());
            return this.soapRsaEncrypter.decrypt(encryptedDecodedSoapSecret);
        } catch (Exception e) {
            throw new IllegalStateException("Error decoding/decrypting SOAP key", e);
        }
    }

    private String encryptSoapSecret(byte[] secret) {
        try {
            byte[] encrypted = this.soapRsaEncrypter.encrypt(secret);
            return Hex.encodeHexString(encrypted);
        } catch (Exception e) {
            throw new IllegalStateException("Error encoding/encrypting SOAP key", e);
        }
    }
}
