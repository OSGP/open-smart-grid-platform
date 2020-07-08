package org.opensmartgridplatform.adapter.protocol.dlms.application.wsclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.opensmartgridplatform.ws.schema.core.secret.management.GetSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.GetSecretsResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.StoreSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.StoreSecretsResponse;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * SOAP Client for SecretManagement
 */
@Component
@Slf4j
public class SecretManagementClient {

    private final WebServiceTemplate webServiceTemplate;

    SecretManagementClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    public GetSecretsResponse getSecretsRequest(GetSecretsRequest request) {

        log.info("Calling SecretManagement.getSecretsRequest over SOAP for device {}", request.getDeviceId());

        return (GetSecretsResponse) this.webServiceTemplate
                .marshalSendAndReceive(request);

    }

    public StoreSecretsResponse storeSecretsRequest(StoreSecretsRequest request) {
        throw new NotImplementedException();
    }

}
