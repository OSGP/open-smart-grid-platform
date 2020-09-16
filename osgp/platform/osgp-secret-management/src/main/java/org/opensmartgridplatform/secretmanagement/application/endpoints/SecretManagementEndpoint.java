/**
 * Copyright 2020 Smart Society Services B.V.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.endpoints;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretType;
import org.opensmartgridplatform.secretmanagement.application.domain.TypedSecret;
import org.opensmartgridplatform.secretmanagement.application.services.SecretManagementService;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.ws.schema.core.secret.management.ActivateSecretRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.ActivateSecretResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.GetSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.GetSecretsResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.OsgpResultType;
import org.opensmartgridplatform.ws.schema.core.secret.management.SecretTypes;
import org.opensmartgridplatform.ws.schema.core.secret.management.StoreSecretRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.StoreSecretResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecrets;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@Slf4j
public class SecretManagementEndpoint {

    private static final String NAMESPACE_URI = "http://www.opensmartgridplatform"
            + ".org/schemas/security/secretmanagement";

    private final SecretManagementService secretManagementService;
    private final SoapEndpointDataTypeConverter converter;

    public SecretManagementEndpoint(final SecretManagementService secretManagementService,
                                    final SoapEndpointDataTypeConverter converter) {
        this.secretManagementService = secretManagementService;
        this.converter = converter;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSecretsRequest")
    @ResponsePayload
    public GetSecretsResponse getSecretsRequest(@RequestPayload final GetSecretsRequest request) throws OsgpException {

        log.info("Handling incoming SOAP request 'getSecretsRequest' for device {}", request.getDeviceId());

        if (log.isDebugEnabled()) {
            log.debug(this.getSecretsRequestToString(request));
        }

        final GetSecretsResponse response = new GetSecretsResponse();

        final SecretTypes soapSecretTypes = request.getSecretTypes();

        if (soapSecretTypes == null) {
            throw new TechnicalException("Missing input: secret types");
        }

        final List<SecretType> secretTypeList = this.converter.convertToSecretTypes(soapSecretTypes);
        final List<TypedSecret> typedSecrets = this.secretManagementService.retrieveSecrets(request.getDeviceId(),
                secretTypeList);

        final TypedSecrets soapTypedSecrets = this.converter.convertToSoapTypedSecrets(typedSecrets);

        response.setTypedSecrets(soapTypedSecrets);
        response.setResult(OsgpResultType.OK);

        log.trace(response.toString());

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "storeSecretsRequest")
    @ResponsePayload
    public StoreSecretResponse storeSecretsRequest(@RequestPayload final StoreSecretRequest request) throws OsgpException {

        log.info("Handling incoming SOAP request 'storeSecretsRequest' for device {}", request.getDeviceId());
        log.trace(request.toString());

        final StoreSecretResponse response = new StoreSecretResponse();

        final TypedSecret typedSecret = this.converter.decryptAndConvertSoapTypedSecret(request.getTypedSecret());

        try {
            this.secretManagementService.storeSecret(request.getDeviceId(), typedSecret);
        } catch(final IOException ioe) {
            //TODO process/rethrow exception
        }

        response.setResult(OsgpResultType.OK);

        log.trace(response.toString());

        return response;
    }

    private String getSecretsRequestToString(final GetSecretsRequest request) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            final JAXBContext ctx = JAXBContext.newInstance(GetSecretsRequest.class);
            final Marshaller marshaller = ctx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(request, baos);
        } catch (final JAXBException e) {
            log.error("Could not serialize GetSecretsRequest", e);
        }
        return baos.toString();
    }

    public ActivateSecretResponse activateSecretRequest(@RequestPayload final ActivateSecretRequest request) throws OsgpException {
        log.info("Handling incoming SOAP request 'activateSecretRequest' for device {}", request.getDeviceId());
        log.trace(request.toString());

        final ActivateSecretResponse response = new ActivateSecretResponse();
        try{
            this.secretManagementService.activateNewSecret(request.getDeviceId(),
                    this.converter.convertToSecretType(request.getType()));
            response.setResult(OsgpResultType.OK);
        } catch(final RuntimeException rte) {
            //TODO process/rethrow exception
        }
        log.trace(response.toString());

        return response;
    }
}
