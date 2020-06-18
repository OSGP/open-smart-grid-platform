package org.opensmartgridplatform.adapter.protocol.dlms.application.wsclient;

import org.apache.commons.lang3.NotImplementedException;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsRequest;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsResponse;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.StoreSecretsRequest;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.StoreSecretsResponse;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * SOAP Client for SecretManagement
 */
@Component
public class SecretManagementClient {

    private final WebServiceTemplate webServiceTemplate;

    SecretManagementClient() {

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // this package must match the package in the <generatePackage> specified in
        // pom.xml
        marshaller.setContextPath("org.opensmartgridplatform.schemas.security.secretmanagement._2020._05");

        this.webServiceTemplate = new WebServiceTemplate(marshaller);
    }

    public GetSecretsResponse getSecretsRequest(GetSecretsRequest request) {

        return (GetSecretsResponse) this.webServiceTemplate
                .marshalSendAndReceive("http://localhost:8080/osgp-secret-management/ws/SecretManagement", request);

    }

    public StoreSecretsResponse storeSecretsRequest(StoreSecretsRequest request) {
        throw new NotImplementedException();
    }

}
