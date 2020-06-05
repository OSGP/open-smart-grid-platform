package org.opensmartgridplatform.secretmgmt.application.endpoints;

import org.apache.tomcat.util.buf.HexUtils;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsRequest;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsResponse;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.OsgpResultType;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.StoreSecretsRequest;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.StoreSecretsResponse;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TechnicalFault;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecrets;
import org.opensmartgridplatform.secretmgmt.application.config.ApplicationConfig;
import org.opensmartgridplatform.secretmgmt.application.domain.SecretType;
import org.opensmartgridplatform.secretmgmt.application.domain.TypedSecret;
import org.opensmartgridplatform.secretmgmt.application.services.SecretManagementService;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.EncryptedSecret;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.Secret;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProvider;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProviderType;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.ArrayList;
import java.util.List;

@Endpoint
public class SecretManagementEndpoint {
    private static final String NAMESPACE_URI = "http://www.opensmartgridplatform.org/schemas/security/secretmanagement/2020/05";
    private static final String KEY_REFERENCE = "1"; //only one key in use

    private SecretManagementService secretManagementService;
    private EncryptionProvider jreEncryptionProvider;

    public SecretManagementEndpoint(SecretManagementService secretManagementService, EncryptionProvider soapSecretEncryptionProvider) {
        this.secretManagementService = secretManagementService;
        this.jreEncryptionProvider = soapSecretEncryptionProvider;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSecretsRequest")
    @ResponsePayload
    public GetSecretsResponse getSecretsRequest(@RequestPayload GetSecretsRequest request) {

        List<org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretType> soapSecretTypeList = request.getSecretTypes().getSecretType();
        List<SecretType> secretTypeList = new ArrayList<>();

        for (org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretType soapSecretType: soapSecretTypeList) {
            SecretType secretType = convertToSecretType(soapSecretType);
            secretTypeList.add(secretType);
        }

        GetSecretsResponse response = new GetSecretsResponse();

        try {
            List<TypedSecret> typedSecrets = secretManagementService.retrieveSecrets(request.getDeviceId(), secretTypeList);
            TypedSecrets soapTypedSecrets = new TypedSecrets();

            List<org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecret> soapTypedSecretList = soapTypedSecrets.getTypedSecret();

            for (TypedSecret typedSecret : typedSecrets) {
                org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecret soapTypedSecret = encryptAndConvertSoapTypedSecret(typedSecret);
                soapTypedSecretList.add(soapTypedSecret);
            }

            response.setTypedSecrets(soapTypedSecrets);
            response.setResult(OsgpResultType.OK);
        } catch (Exception e) {
            response.setResult(OsgpResultType.NOT_OK);
            TechnicalFault fault = new TechnicalFault();
            fault.setMessage(e.getMessage());
            fault.setComponent(ApplicationConfig.COMPONENT_NAME);
            response.setTechnicalFault(fault);
        }

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "storeSecretsRequest")
    @ResponsePayload
    public StoreSecretsResponse storeSecretsRequest(@RequestPayload StoreSecretsRequest request) throws Exception {

        List<org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecret> soapTypedSecrets = request.getTypedSecrets().getTypedSecret();
        List<TypedSecret> typedSecrets = new ArrayList<>();

        StoreSecretsResponse response = new StoreSecretsResponse();

        for (org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecret soapTypedSecret : soapTypedSecrets) {
            typedSecrets.add(decryptAndConvertSoapTypedSecret(soapTypedSecret));
        }

        try {
            secretManagementService.storeSecrets(request.getDeviceId(), typedSecrets);
            response.setResult(OsgpResultType.OK);
        } catch (Exception e) {
            response.setResult(OsgpResultType.NOT_OK);
            TechnicalFault fault = new TechnicalFault();
            fault.setMessage(e.getMessage());
            fault.setComponent(ApplicationConfig.COMPONENT_NAME);
            response.setTechnicalFault(fault);
        }

        return response;
    }

    private SecretType convertToSecretType(org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretType soapSecretType) {
        SecretType secretType = SecretType.valueOf(soapSecretType.value());
        return secretType;
    }

    private org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretType convertToSoapSecretType(SecretType secretType) {
        org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretType soapSecretType = org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretType.fromValue(secretType.name());
        return soapSecretType;
    }

    private org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecret encryptAndConvertSoapTypedSecret(TypedSecret typedSecret) throws Exception {
        org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecret soapTypedSecret = new org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecret();

        String encodedSecret = typedSecret.getSecret();
        byte[] rawSecret = HexUtils.fromHexString(encodedSecret);
        Secret secret = new Secret(rawSecret);
        EncryptedSecret encryptedSecret = jreEncryptionProvider.encrypt(secret, KEY_REFERENCE);
        soapTypedSecret.setSecret(HexUtils.toHexString(encryptedSecret.getSecret()));

        SecretType secretType = typedSecret.getSecretType();
        soapTypedSecret.setType(convertToSoapSecretType(secretType));

        return soapTypedSecret;
    }

    private TypedSecret decryptAndConvertSoapTypedSecret(org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecret soapTypedSecret) throws Exception {
        TypedSecret typedSecret = new TypedSecret();

        byte[] rawEncryptedSecret = HexUtils.fromHexString(soapTypedSecret.getSecret());
        EncryptedSecret encryptedSecret = new EncryptedSecret(EncryptionProviderType.JRE, rawEncryptedSecret);
        Secret decryptedSecret = jreEncryptionProvider.decrypt(encryptedSecret, KEY_REFERENCE);

        typedSecret.setSecret(HexUtils.toHexString(decryptedSecret.getSecret()));
        typedSecret.setSecretType(convertToSecretType(soapTypedSecret.getType()));

        return typedSecret;
    }


}
