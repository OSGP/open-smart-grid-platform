/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.wsclient;

import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.ws.schema.core.secret.management.ActivateSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.ActivateSecretsResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.GenerateAndStoreSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.GenerateAndStoreSecretsResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.GetSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.GetSecretsResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.HasNewSecretRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.HasNewSecretResponse;
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
        log.info("Calling SecretManagement.storeSecretsRequest over SOAP for device {}", request.getDeviceId());

        return (StoreSecretsResponse) this.webServiceTemplate
                .marshalSendAndReceive(request);
    }

    public ActivateSecretsResponse activateSecretsRequest(ActivateSecretsRequest request) {
        log.info("Calling SecretManagement.activateSecretsRequest over SOAP for device {}",
                request.getDeviceId());

        return (ActivateSecretsResponse) this.webServiceTemplate
                .marshalSendAndReceive(request);
    }

    public HasNewSecretResponse hasNewSecretRequest(HasNewSecretRequest request) {
        log.info("Calling SecretManagement.hasNewSecretsRequest over SOAP for device {}",
                request.getDeviceId());

        return (HasNewSecretResponse) this.webServiceTemplate
                .marshalSendAndReceive(request);
    }

    public GenerateAndStoreSecretsResponse generateAndStoreSecrets(GenerateAndStoreSecretsRequest request) {
        log.info("Calling SecretManagement.generateAndStoreSecrets over SOAP for device {}",
                request.getDeviceId());

        return (GenerateAndStoreSecretsResponse) this.webServiceTemplate
                .marshalSendAndReceive(request);
    }
}
