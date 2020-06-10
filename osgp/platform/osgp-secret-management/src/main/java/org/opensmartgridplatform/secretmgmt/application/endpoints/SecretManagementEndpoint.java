package org.opensmartgridplatform.secretmgmt.application.endpoints;

import org.apache.tomcat.util.buf.HexUtils;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsRequest;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsResponse;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.OsgpResultType;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretTypes;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.StoreSecretsRequest;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.StoreSecretsResponse;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TechnicalFault;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecrets;
import org.opensmartgridplatform.secretmgmt.application.domain.SecretType;
import org.opensmartgridplatform.secretmgmt.application.domain.TypedSecret;
import org.opensmartgridplatform.secretmgmt.application.exception.TechnicalServiceFaultException;
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

import static org.opensmartgridplatform.secretmgmt.application.config.ApplicationConfig.COMPONENT_NAME;

@Endpoint
public class SecretManagementEndpoint {
    private static final String NAMESPACE_URI = "http://www.opensmartgridplatform.org/schemas/security/secretmanagement/2020/05";
    private static final String KEY_REFERENCE = "1"; //only one key in use

    private final SecretManagementService secretManagementService;
    private final EncryptionProvider jreEncryptionProvider;

    public SecretManagementEndpoint(SecretManagementService secretManagementService, EncryptionProvider soapSecretEncryptionProvider) {
        this.secretManagementService = secretManagementService;
        this.jreEncryptionProvider = soapSecretEncryptionProvider;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSecretsRequest")
    @ResponsePayload
    public GetSecretsResponse getSecretsRequest(@RequestPayload GetSecretsRequest request) {

        GetSecretsResponse response = new GetSecretsResponse();

        try {
            SecretTypes soapSecretTypes = request.getSecretTypes();
            List<org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretType> soapSecretTypeList = soapSecretTypes.getSecretType();

            if (soapSecretTypes == null) {
                throw new TechnicalServiceFaultException("Missing input: secret types");
            }

            List<SecretType> secretTypeList = new ArrayList<>();

            for (org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretType soapSecretType: soapSecretTypeList) {
                SecretType secretType = convertToSecretType(soapSecretType);
                secretTypeList.add(secretType);
            }

            List<TypedSecret> typedSecrets = secretManagementService.retrieveSecrets(request.getDeviceId(), secretTypeList);
            TypedSecrets soapTypedSecrets = new TypedSecrets();

            List<org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecret> soapTypedSecretList = soapTypedSecrets.getTypedSecret();

            for (TypedSecret typedSecret : typedSecrets) {
                org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecret soapTypedSecret = encryptAndConvertSoapTypedSecret(typedSecret);
                soapTypedSecretList.add(soapTypedSecret);
            }

            response.setTypedSecrets(soapTypedSecrets);
            response.setResult(OsgpResultType.OK);

            return response;
        }
        catch (Exception e) {
            throw new TechnicalServiceFaultException(e.getMessage(), e, createTechnicalFaultFromException(e));
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "storeSecretsRequest")
    @ResponsePayload
    public StoreSecretsResponse storeSecretsRequest(@RequestPayload StoreSecretsRequest request) {

        StoreSecretsResponse response = new StoreSecretsResponse();

        try {
            TypedSecrets soapTypedSecrets = request.getTypedSecrets();

            if (soapTypedSecrets == null) {
                throw new TechnicalServiceFaultException("Missing input: typed secrets");
            }

            List<org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecret> soapTypedSecretsList = soapTypedSecrets.getTypedSecret();
            List<TypedSecret> typedSecretList = new ArrayList<>();

            for (org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecret soapTypedSecret : soapTypedSecretsList) {
                typedSecretList.add(decryptAndConvertSoapTypedSecret(soapTypedSecret));
            }

            secretManagementService.storeSecrets(request.getDeviceId(), typedSecretList);
            response.setResult(OsgpResultType.OK);

            return response;
        }
        catch (Exception e) {
            throw new TechnicalServiceFaultException(e.getMessage(), e, createTechnicalFaultFromException(e));
        }
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

    private TechnicalFault createTechnicalFaultFromException(Exception e) {
        TechnicalFault fault = new TechnicalFault();
        fault.setMessage(e.getMessage());
        fault.setComponent(COMPONENT_NAME);
        if (e.getCause() != null) {
            fault.setInnerException(e.getCause().toString());
            fault.setInnerMessage(e.getCause().getMessage());
        }
        return fault;
    }

}
