/*
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
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
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

    private final RsaEncryptionProvider rsaEncryptionProvider;
    private final SecretManagementClient secretManagementClient;

    public SecretManagementService(RsaEncryptionProvider rsaEncryptionProvider,
            SecretManagementClient secretManagementClient) {
        this.rsaEncryptionProvider = rsaEncryptionProvider;
        this.secretManagementClient = secretManagementClient;
    }

    @Override
    public byte[] reEncryptKey(byte[] externallyEncryptedKey, SecurityKeyType keyType) throws FunctionalException {
        throw new NotImplementedException();
    }

    @Override
    public byte[] decryptKey(byte[] encryptedKey, SecurityKeyType keyType) throws ProtocolAdapterException {
        throw new NotImplementedException();
    }

    @Override
    public byte[] encryptKey(byte[] plainKey, SecurityKeyType keyType) throws ProtocolAdapterException {
        throw new NotImplementedException();
    }

    @Override
    public byte[] getDlmsMasterKey(String deviceIdentification) {
        throw new NotImplementedException();
    }

    @Override
    public byte[] getDlmsAuthenticationKey(String deviceIdentification) {

        GetSecretsResponse response;

        try {
            GetSecretsRequest request = getSoapRequestForKey(deviceIdentification,
                SecretType.E_METER_AUTHENTICATION_KEY);

            response = secretManagementClient.getSecretsRequest(request);
        }
        catch (Exception e) {
            throw new ConnectionException("Error while communicating with secret management "
                    + "(getDlmsAuthenticationKey)", e);
        }

        try {
            Optional<TypedSecret> optionalTypedSecret = getTypedSecretFromSoapResponse(response,
                    SecretType.E_METER_AUTHENTICATION_KEY);

            byte[] decryptedKey = decryptSoapSecret(deviceIdentification, optionalTypedSecret.orElseThrow(
                    () -> new IllegalStateException("Secret not found:" + deviceIdentification)));

            log.trace("DlmsAuthenticationKey for device " + deviceIdentification + " is " + Hex.encodeHexString(decryptedKey));

            return decryptedKey;

        } catch (Exception e) {
            throw new EncrypterException("Error while retrieving authentication key", e);
        }
    }

    @Override
    public byte[] getDlmsGlobalUnicastEncryptionKey(String deviceIdentification) {

        GetSecretsResponse response;

        try {
            GetSecretsRequest request = getSoapRequestForKey(deviceIdentification,
                    SecretType.E_METER_ENCRYPTION_KEY_UNICAST);
            response = secretManagementClient.getSecretsRequest(request);
        }
        catch(Exception e) {
            throw new ConnectionException("Error while communicating with secret management "
                    + "(getDlmsGlobalUnicastEncryptionKey)", e);
        }

        try {
            Optional<TypedSecret> optionalTypedSecret = getTypedSecretFromSoapResponse(response,
                    SecretType.E_METER_ENCRYPTION_KEY_UNICAST);

            byte[] decryptedKey = decryptSoapSecret(deviceIdentification, optionalTypedSecret.orElseThrow(
                    () -> new IllegalStateException("Secret not found:" + deviceIdentification)));

            log.trace("DlmsGlobalUnicastEncryptionKey for device " + deviceIdentification + " is " + Hex.encodeHexString(decryptedKey));

            return decryptedKey;

        } catch (Exception e) {
            throw new EncrypterException("Error while retrieving global unicast key", e);
        }
    }

    @Override
    public byte[] getMbusDefaultKey(String mbusDeviceIdentification) {
        throw new NotImplementedException();
    }

    @Override
    public byte[] getMbusUserKey(String mbusDeviceIdentification) {
        throw new NotImplementedException();
    }

    @Override
    public byte[] getDlmsPassword(String deviceIdentification) {
        throw new NotImplementedException();
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

    @Override
    public byte[] encryptMbusUserKey(byte[] mbusDefaultKey, byte[] mbusUserKey) throws ProtocolAdapterException {
        throw new NotImplementedException();
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
