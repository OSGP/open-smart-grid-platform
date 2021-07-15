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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.apache.commons.codec.binary.Hex;
import org.opensmartgridplatform.adapter.protocol.dlms.application.wsclient.SecretManagementClient;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.shared.security.RsaEncrypter;
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
import org.opensmartgridplatform.ws.schema.core.secret.management.StoreSecretsResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecret;
import org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecrets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
/**
 * Service for storing, activating and retrieving device keys.
 * Also performs RSA encryption/decryption operations for SOAP messaging purposes.
 */
public class SecretManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecretManagementService.class);
    private final RsaEncrypter soapRsaEncrypter;
    private final SecretManagementClient secretManagementClient;

    public SecretManagementService(final RsaEncrypter soapRsaEncrypter,
            final SecretManagementClient secretManagementClient) {
        this.soapRsaEncrypter = soapRsaEncrypter;
        this.secretManagementClient = secretManagementClient;
    }

    /**
     * Retrieve an active key of a certain type for a specified device
     *
     * @param deviceIdentification
     *         the device identification string of the device
     * @param keyType
     *         the requested key type
     *
     * @return the key or NULL if not present
     */
    public byte[] getKey(final String deviceIdentification, final SecurityKeyType keyType) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Retrieving {} for device {}", keyType.name(), deviceIdentification);
        }
        return this.getKeys(deviceIdentification, Arrays.asList(keyType)).get(keyType);
    }

    /**
     * Retrieves the active keys of requested types for a specified device
     *
     * @param deviceIdentification
     *         the device identification string of the device
     * @param keyTypes
     *         the requested key types
     *
     * @return the requested keys in a map by key type, with value NULL if not present
     */
    public Map<SecurityKeyType, byte[]> getKeys(final String deviceIdentification,
            final List<SecurityKeyType> keyTypes) {
        final GetSecretsRequest request = this.createGetSecretsRequest(deviceIdentification, keyTypes);
        final GetSecretsResponse response = this.secretManagementClient.getSecretsRequest(request);
        this.validateGetResponse(keyTypes, response);
        return this.convertSoapSecretsToSecretMapByType(response.getTypedSecrets().getTypedSecret());
    }

    /**
     * Retrieve a new (not yet activated) key of a certain type for a specified device
     *
     * @param deviceIdentification
     *         the device identification string of the device
     * @param keyType
     *         the requested key type
     *
     * @return the key or NULL if not present
     */
    public byte[] getNewKey(final String deviceIdentification, final SecurityKeyType keyType) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Retrieving new {} for device {}", keyType.name(), deviceIdentification);
        }
        return this.getNewKeys(deviceIdentification, Arrays.asList(keyType)).get(keyType);
    }

    /**
     * Retrieves the new (not yet activated) keys of requested types for a specified device
     *
     * @param deviceIdentification
     *         the device identification string of the device
     * @param keyTypes
     *         the requested key types
     *
     * @return the requested keys in a map by key type, with value NULL if not present
     */
    public Map<SecurityKeyType, byte[]> getNewKeys(final String deviceIdentification,
            final List<SecurityKeyType> keyTypes) {
        final GetNewSecretsRequest request = this.createGetNewSecretsRequest(deviceIdentification, keyTypes);
        final GetNewSecretsResponse response = this.secretManagementClient.getNewSecretsRequest(request);
        this.validateGetNewResponse(keyTypes, response);
        return this.convertSoapSecretsToSecretMapByType(response.getTypedSecrets().getTypedSecret());
    }

    private void validateGetResponse(final List<SecurityKeyType> keyTypes, final GetSecretsResponse response) {
        this.validateOsgpResultAndTypedSecrets(response.getResult(), response.getTechnicalFault(),
                response.getTypedSecrets(), keyTypes.size());
    }

    private void validateGetNewResponse(final List<SecurityKeyType> keyTypes, final GetNewSecretsResponse response) {
        this.validateOsgpResultAndTypedSecrets(response.getResult(), response.getTechnicalFault(),
                response.getTypedSecrets(), keyTypes.size());
    }

    private Map<SecurityKeyType, byte[]> convertSoapSecretsToSecretMapByType(final List<TypedSecret> soapSecrets) {
        final Function<TypedSecret, SecurityKeyType> convertType = ts -> SecurityKeyType.fromSecretType(ts.getType());
        final Function<TypedSecret, byte[]> convertSecret = ts -> this.decryptSoapSecret(ts, false);
        final Map<SecurityKeyType, byte[]> decryptedKeysByType = new EnumMap<>(SecurityKeyType.class);
        soapSecrets.forEach(ts -> decryptedKeysByType.put(convertType.apply(ts), convertSecret.apply(ts)));
        return decryptedKeysByType;
    }

    private GetSecretsRequest createGetSecretsRequest(final String deviceIdentification,
            final List<SecurityKeyType> keyTypes) {
        final GetSecretsRequest request = new GetSecretsRequest();
        request.setDeviceId(deviceIdentification);
        request.setSecretTypes(new SecretTypes());
        final List<SecretType> secretTypeList = request.getSecretTypes().getSecretType();
        keyTypes.stream().forEach(kt -> secretTypeList.add(kt.toSecretType()));
        return request;
    }

    private GetNewSecretsRequest createGetNewSecretsRequest(final String deviceIdentification,
            final List<SecurityKeyType> keyTypes) {
        final GetNewSecretsRequest request = new GetNewSecretsRequest();
        request.setDeviceId(deviceIdentification);
        request.setSecretTypes(new SecretTypes());
        final List<SecretType> secretTypeList = request.getSecretTypes().getSecretType();
        keyTypes.stream().forEach(kt -> secretTypeList.add(kt.toSecretType()));
        return request;
    }

    /**
     * Store new key
     * <p>
     * A new key is a security key with a device which status NEW.
     * This status is used when the new key is known, but not yet set on the device.
     * <p>
     * <strong>CAUTION:</strong> Only call this method when a successful
     * connection with the device has been set up (that is: a valid
     * communication key that works is known), and you are sure any existing new
     * key data that is not activated yet (for instance a new key stored earlier in an
     * attempt to replace the communication key that got aborted).<br>
     * <p>
     * The moment the new key is known to be transferred to the device, make
     * sure to activate it by calling
     * {@link #activateNewKey(String, SecurityKeyType)}.
     *
     * @param deviceIdentification
     *         DLMS device id
     * @param key
     *         key to store, unencrypted
     * @param keyType
     *         type of key
     *
     * @see #activateNewKey(String, SecurityKeyType)
     */
    public void storeNewKey(final String deviceIdentification, final SecurityKeyType keyType, byte[] key) {
        final Map<SecurityKeyType, byte[]> keysByType = new EnumMap<>(SecurityKeyType.class);
        keysByType.put(keyType, key);
        this.storeNewKeys(deviceIdentification, keysByType);
    }

    public void storeNewKeys(final String deviceIdentification, final Map<SecurityKeyType, byte[]> keysByType) {
        this.validateKeys(keysByType);
        final TypedSecrets typedSecrets = new TypedSecrets();
        final List<TypedSecret> typedSecretList = typedSecrets.getTypedSecret();
        for (Map.Entry<SecurityKeyType, byte[]> entry : keysByType.entrySet()) {
            TypedSecret ts = new TypedSecret();
            ts.setType(entry.getKey().toSecretType());
            ts.setSecret(this.encryptSoapSecret(entry.getValue(), true));
            typedSecretList.add(ts);
        }
        final StoreSecretsRequest request = this.createStoreSecretsRequest(deviceIdentification, typedSecrets);
        StoreSecretsResponse response = null;
        try {
            response = this.secretManagementClient.storeSecretsRequest(request);
        } catch (RuntimeException exc) {
            throw new IllegalStateException("Could not store keys: unexpected exception occured", exc);
        }
        if (response == null) {
            throw new IllegalStateException("Could not store keys: NULL response");
        } else if (!OsgpResultType.OK.equals(response.getResult())) {
            throw new IllegalStateException(
                    String.format("Could not store keys: result=%s; fault=%s", response.getResult(),
                            response.getTechnicalFault()));
        }
    }

    private void validateKeys(final Map<SecurityKeyType, byte[]> keysByType) {
        final long nrNulls = keysByType.values().stream().filter(Objects::isNull).count();
        if (nrNulls > 0) {
            throw new IllegalArgumentException(
                    String.format("Provided %s keys, %s of which were NULL", keysByType.size(), nrNulls));
        }
    }

    private StoreSecretsRequest createStoreSecretsRequest(final String deviceIdentification,
            final TypedSecrets typedSecrets) {
        final StoreSecretsRequest request = new StoreSecretsRequest();
        request.setDeviceId(deviceIdentification);
        request.setTypedSecrets(typedSecrets);
        return request;
    }

    /**
     * Updates the state of a new key from 'new' to 'active'
     * <p>
     * This method should be called to activate a new key stored with
     * {@link #storeNewKeys(String, Map)} after it has
     * been confirmed to be set on the device.
     *
     * @param deviceIdentification
     *         DLMS device id
     * @param keyType
     *         type of key
     *
     * @see #storeNewKeys(String, Map)
     */
    public void activateNewKey(final String deviceIdentification, final SecurityKeyType keyType) {
        this.activateNewKeys(deviceIdentification, Arrays.asList(keyType));
    }

    public void activateNewKeys(final String deviceIdentification, final List<SecurityKeyType> keyTypes) {
        final ActivateSecretsRequest request = new ActivateSecretsRequest();
        request.setDeviceId(deviceIdentification);
        request.setSecretTypes(new SecretTypes());
        final List<SecretType> secretTypeList = request.getSecretTypes().getSecretType();
        keyTypes.forEach(kt -> secretTypeList.add(kt.toSecretType()));
        this.secretManagementClient.activateSecretsRequest(request);
    }

    public boolean hasNewSecretOfType(final String deviceIdentification, final SecurityKeyType keyType) {
        final HasNewSecretRequest request = new HasNewSecretRequest();
        request.setDeviceId(deviceIdentification);
        request.setSecretType(keyType.toSecretType());
        final HasNewSecretResponse response = this.secretManagementClient.hasNewSecretRequest(request);
        return response.isHasNewSecret();
    }

    public byte[] generate128BitsKeyAndStoreAsNewKey(final String deviceIdentification, final SecurityKeyType keyType) {
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
    public Map<SecurityKeyType, byte[]> generate128BitsKeysAndStoreAsNewKeys(final String deviceIdentification,
            final List<SecurityKeyType> keyTypes) {
        final SecretTypes secretTypes = new SecretTypes();
        final GenerateAndStoreSecretsRequest request = this
                .createGenerateAndStoreSecretsRequest(deviceIdentification, secretTypes);
        secretTypes.getSecretType().addAll(keyTypes.stream().map(SecurityKeyType::toSecretType).collect(toList()));

        final GenerateAndStoreSecretsResponse response = this.secretManagementClient.generateAndStoreSecrets(request);
        final TypedSecrets typedSecrets = response.getTypedSecrets();
        final List<TypedSecret> typedSecretList = typedSecrets.getTypedSecret();
        this.validateGenerateAndStoreResponse(keyTypes, response, typedSecretList);
        return this.convertSoapSecretsToSecretMapByType(typedSecrets.getTypedSecret());
    }

    private GenerateAndStoreSecretsRequest createGenerateAndStoreSecretsRequest(final String deviceIdentification,
            final SecretTypes secretTypes) {
        final GenerateAndStoreSecretsRequest request = new GenerateAndStoreSecretsRequest();
        request.setDeviceId(deviceIdentification);
        request.setSecretTypes(secretTypes);
        return request;
    }

    private void validateGenerateAndStoreResponse(final List<SecurityKeyType> keyTypes,
            final GenerateAndStoreSecretsResponse response, final List<TypedSecret> typedSecretList) {
        this.validateOsgpResultAndTypedSecrets(response.getResult(), response.getTechnicalFault(),
                response.getTypedSecrets(), keyTypes.size());
        typedSecretList.forEach(ts -> {
            if (ts.getSecret() == null) {
                throw new IllegalStateException(String.format("Generated a NULL key (key_type=%s)", ts.getType()));
            }
        });
    }

    private void validateOsgpResultAndTypedSecrets(final OsgpResultType result, final Object fault,
            final TypedSecrets typedSecrets, final int expectedNrKeys) {
        if (!OsgpResultType.OK.equals(result)) {
            throw new IllegalStateException("Could not process keys in secret-mgmt: " + fault);
        } else if (typedSecrets == null || typedSecrets.getTypedSecret() == null) {
            throw new IllegalStateException("No secrets in response");
        } else if (expectedNrKeys != typedSecrets.getTypedSecret().size()) {
            throw new IllegalStateException(
                    String.format("Unexpected number of secrets in response: expected %s but found %s", expectedNrKeys,
                            typedSecrets.getTypedSecret().size()));
        }
    }

    private byte[] decryptSoapSecret(final TypedSecret typedSecret, final boolean exceptionOnNull) {
        boolean nullValue = typedSecret.getSecret() == null || typedSecret.getSecret().isEmpty();
        if (exceptionOnNull && nullValue) {
            throw new IllegalArgumentException("Cannot decrypt NULL value");
        } else if (!exceptionOnNull && nullValue) {
            return null;
        }
        try {
            byte[] encryptedDecodedSoapSecret = Hex.decodeHex(typedSecret.getSecret());
            return this.soapRsaEncrypter.decrypt(encryptedDecodedSoapSecret);
        } catch (Exception e) {
            throw new IllegalStateException("Error decoding/decrypting SOAP key", e);
        }
    }

    private String encryptSoapSecret(final byte[] secret, final boolean exceptionOnNull) {
        boolean nullValue = secret == null || secret.length == 0;
        if (exceptionOnNull && nullValue) {
            throw new IllegalArgumentException("Cannot encrypt NULL value");
        } else if (!exceptionOnNull && nullValue) {
            return null;
        }
        try {
            byte[] encrypted = this.soapRsaEncrypter.encrypt(secret);
            return Hex.encodeHexString(encrypted);
        } catch (Exception e) {
            throw new IllegalStateException("Error encoding/encrypting SOAP key", e);
        }
    }
}
