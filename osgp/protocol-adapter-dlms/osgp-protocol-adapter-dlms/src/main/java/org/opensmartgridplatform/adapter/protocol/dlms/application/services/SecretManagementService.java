package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.NotImplementedException;
import org.opensmartgridplatform.adapter.protocol.dlms.application.wsclient.SecretManagementClient;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsRequest;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsResponse;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretType;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretTypes;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecret;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecrets;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.security.EncryptedSecret;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;
import org.opensmartgridplatform.shared.security.Secret;
import org.opensmartgridplatform.shared.security.providers.RsaEncryptionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "secretManagementService")
@Transactional
public class SecretManagementService implements SecurityKeyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecretManagementService.class);
    private RsaEncryptionProvider rsaEncryptionProvider;
    private SecretManagementClient secretManagementClient;

    public SecretManagementService(RsaEncryptionProvider rsaEncryptionProvider, SecretManagementClient secretManagementClient)
    {
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

        try {
            GetSecretsRequest request = getSoapRequestForKey(deviceIdentification, SecretType.E_METER_AUTHENTICATION_KEY);
            GetSecretsResponse response = secretManagementClient.getSecretsRequest(request);
            Optional<TypedSecret> optionalTypedSecret = getTypedSecretFromSoapResponse(response, SecretType.E_METER_AUTHENTICATION_KEY);

            return decryptSoapSecret(deviceIdentification,
                    optionalTypedSecret.orElseThrow(()->new IllegalStateException("Secret not found:" + deviceIdentification)));
        }
        catch(Exception e) {
            LOGGER.error("Error while retrieving authentication key", e);
        }
        return new byte[0];
    }

    @Override
    public byte[] getDlmsGlobalUnicastEncryptionKey(String deviceIdentification) {

        try {
            GetSecretsRequest request = getSoapRequestForKey(deviceIdentification, SecretType.E_METER_ENCRYPTION_KEY_UNICAST);
            GetSecretsResponse response = secretManagementClient.getSecretsRequest(request);
            Optional<TypedSecret> optionalTypedSecret = getTypedSecretFromSoapResponse(response, SecretType.E_METER_ENCRYPTION_KEY_UNICAST);

            return decryptSoapSecret(deviceIdentification,
                    optionalTypedSecret.orElseThrow(()->new IllegalStateException("Secret not found:" + deviceIdentification)));
        }
        catch(Exception e) {
            LOGGER.error("Error while retrieving encryption key", e);
        }
        return new byte[0];
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
