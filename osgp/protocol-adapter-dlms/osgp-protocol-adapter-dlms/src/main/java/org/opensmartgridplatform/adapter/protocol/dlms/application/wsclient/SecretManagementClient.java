package org.opensmartgridplatform.adapter.protocol.dlms.application.wsclient;

import org.apache.commons.lang3.NotImplementedException;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsRequest;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsResponse;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.StoreSecretsRequest;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.StoreSecretsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * SOAP Client for SecretManagement
 */
@Component
public class SecretManagementClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecretManagementClient.class);

    private final WebServiceTemplate webServiceTemplate;

    SecretManagementClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    public GetSecretsResponse getSecretsRequest(GetSecretsRequest request) {

        LOGGER.info("Calling SecretManagement.getSecretsRequest over SOAP for device {}", request.getDeviceId());

        return (GetSecretsResponse) this.webServiceTemplate
                .marshalSendAndReceive(request);

    }

    public StoreSecretsResponse storeSecretsRequest(StoreSecretsRequest request) {
        throw new NotImplementedException();
    }

}
