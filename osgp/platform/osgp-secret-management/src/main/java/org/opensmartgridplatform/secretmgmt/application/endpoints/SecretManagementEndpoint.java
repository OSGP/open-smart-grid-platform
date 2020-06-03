package org.opensmartgridplatform.secretmgmt.application.endpoints;

import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsRequest;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsResponse;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.OsgpResultType;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.StoreSecretsRequest;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.StoreSecretsResponse;
import org.opensmartgridplatform.secretmgmt.application.services.SecretMangementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class SecretManagementEndpoint {
    private static final String NAMESPACE_URI = "http://www.opensmartgridplatform.org/schemas/security/secretmanagement/2020/05";

    private SecretMangementService secretManagementService;

    @Autowired
    public void MyEndpoint(SecretMangementService secretManagementService) {
        this.secretManagementService = secretManagementService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSecretsRequest")
    @ResponsePayload
    public GetSecretsResponse getSecretsRequest(@RequestPayload GetSecretsRequest request) {
        GetSecretsResponse response = new GetSecretsResponse();
        response.setResult(OsgpResultType.OK);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "storeSecretsRequest")
    @ResponsePayload
    public StoreSecretsResponse storeSecretsRequest(@RequestPayload StoreSecretsRequest request) {
        StoreSecretsResponse response = new StoreSecretsResponse();
        response.setResult(OsgpResultType.OK);
        return response;
    }
}