package org.opensmartgridplatform.secretmgmt.application.endpoints;

import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsRequest;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsResponse;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.OsgpResultType;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.StoreSecretsRequest;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.StoreSecretsResponse;
import org.opensmartgridplatform.secretmgmt.application.domain.TypedSecret;
import org.opensmartgridplatform.secretmgmt.application.services.SecretMangementService;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProvider;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.ArrayList;
import java.util.List;

@Endpoint
public class SecretManagementEndpoint {
    private static final String NAMESPACE_URI = "http://www.opensmartgridplatform.org/schemas/security/secretmanagement/2020/05";

    private SecretMangementService secretManagementService;
    private EncryptionProvider jreEncryptionProvider;

    public void SecretManagementEndpoint(SecretMangementService secretManagementService, EncryptionProvider soapSecretEncryptionProvider) {
        this.secretManagementService = secretManagementService;
        this.jreEncryptionProvider = soapSecretEncryptionProvider;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSecretsRequest")
    @ResponsePayload
    public GetSecretsResponse getSecretsRequest(@RequestPayload GetSecretsRequest request) {
        //get secrets from service
        // secretManagementService.retrieveSecrets();
        //encrypt secrets for soap
        GetSecretsResponse response = new GetSecretsResponse();
        response.setResult(OsgpResultType.OK);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "storeSecretsRequest")
    @ResponsePayload
    public StoreSecretsResponse storeSecretsRequest(@RequestPayload StoreSecretsRequest request) {

        StoreSecretsResponse response = new StoreSecretsResponse();

        List<TypedSecret> typedSecrets = new ArrayList<>();

        //map request.get
        try {
            secretManagementService.storeSecrets(request.getDeviceId(), typedSecrets);
            response.setResult(OsgpResultType.OK);
        }
        catch(Exception e) {
            response.setResult(OsgpResultType.NOT_OK);
            //response.setTechnicalFault();
        }

        return response;
    }
}