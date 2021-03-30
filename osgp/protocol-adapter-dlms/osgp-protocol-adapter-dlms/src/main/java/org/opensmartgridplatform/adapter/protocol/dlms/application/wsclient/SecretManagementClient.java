/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.wsclient;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.opensmartgridplatform.ws.schema.core.secret.management.ActivateSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.ActivateSecretsResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.GenerateAndStoreSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.GenerateAndStoreSecretsResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.GetNewSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.GetNewSecretsResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.GetSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.GetSecretsResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.HasNewSecretRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.HasNewSecretResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.StoreSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.StoreSecretsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.xml.transform.StringSource;

/**
 * SOAP Client for SecretManagement
 */
@Component
public class SecretManagementClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecretManagementClient.class);
    private static final String NAMESPACE_URI = "http://www.opensmartgridplatform"
            + ".org/schemas/security/secretmanagement";
    private static final String CORRELATION_UID = "correlationUid";

    private final WebServiceTemplate webServiceTemplate;

    SecretManagementClient(final WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    private WebServiceMessageCallback createCorrelationHeaderCallback(final String correlationUid) {
        return message -> {
            try {
                final SoapMessage soapMessage = (SoapMessage) message;
                final SoapHeader header = soapMessage.getSoapHeader();
                final String headerXml = String.format("<%1$s xmlns=\"%2$s\">%3$s</%1$s>", CORRELATION_UID,
                        NAMESPACE_URI, correlationUid);
                final StringSource headerSource = new StringSource(headerXml);
                final Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.transform(headerSource, header.getResult());
            } catch (final TransformerException e) {
                LOGGER.error("Exception during SOAP header callback generation", e);
            }
        };
    }

    public GetSecretsResponse getSecretsRequest(final String correlationUid, final GetSecretsRequest request) {

        LOGGER.info("Calling SecretManagement.getSecretsRequest over SOAP for device {}", request.getDeviceId());
        return (GetSecretsResponse) this.webServiceTemplate.marshalSendAndReceive(request,
                this.createCorrelationHeaderCallback(correlationUid));
    }

    public GetNewSecretsResponse getNewSecretsRequest(final String correlationUid, final GetNewSecretsRequest request) {

        LOGGER.info("Calling SecretManagement.getNewSecretsRequest over SOAP for device {}", request.getDeviceId());

        return (GetNewSecretsResponse) this.webServiceTemplate.marshalSendAndReceive(request,
                this.createCorrelationHeaderCallback(correlationUid));
    }

    public StoreSecretsResponse storeSecretsRequest(final String correlationUid, final StoreSecretsRequest request) {
        LOGGER.info("Calling SecretManagement.storeSecretsRequest over SOAP for device {}", request.getDeviceId());

        return (StoreSecretsResponse) this.webServiceTemplate.marshalSendAndReceive(request,
                this.createCorrelationHeaderCallback(correlationUid));
    }

    public ActivateSecretsResponse activateSecretsRequest(final String correlationUid,
            final ActivateSecretsRequest request) {
        LOGGER.info("Calling SecretManagement.activateSecretsRequest over SOAP for device {}", request.getDeviceId());

        return (ActivateSecretsResponse) this.webServiceTemplate.marshalSendAndReceive(request,
                this.createCorrelationHeaderCallback(correlationUid));
    }

    public HasNewSecretResponse hasNewSecretRequest(final String correlationUid, final HasNewSecretRequest request) {
        LOGGER.info("Calling SecretManagement.hasNewSecretsRequest over SOAP for device {}", request.getDeviceId());

        return (HasNewSecretResponse) this.webServiceTemplate.marshalSendAndReceive(request,
                this.createCorrelationHeaderCallback(correlationUid));
    }

    public GenerateAndStoreSecretsResponse generateAndStoreSecrets(final String correlationUid,
            final GenerateAndStoreSecretsRequest request) {
        LOGGER.info("Calling SecretManagement.generateAndStoreSecrets over SOAP for device {}", request.getDeviceId());

        return (GenerateAndStoreSecretsResponse) this.webServiceTemplate.marshalSendAndReceive(request,
                this.createCorrelationHeaderCallback(correlationUid));
    }
}
