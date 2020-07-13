/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.endpoints;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretType;
import org.opensmartgridplatform.secretmanagement.application.domain.TypedSecret;
import org.opensmartgridplatform.secretmanagement.application.services.SecretManagementService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.ws.schema.core.secret.management.GetSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.GetSecretsResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.OsgpResultType;
import org.opensmartgridplatform.ws.schema.core.secret.management.SecretTypes;
import org.opensmartgridplatform.ws.schema.core.secret.management.StoreSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.StoreSecretsResponse;
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

    public SecretManagementEndpoint(SecretManagementService secretManagementService,
            SoapEndpointDataTypeConverter converter) {
        this.secretManagementService = secretManagementService;
        this.converter = converter;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSecretsRequest")
    @ResponsePayload
    public GetSecretsResponse getSecretsRequest(@RequestPayload GetSecretsRequest request) throws OsgpException {

        log.info("Handling incoming SOAP request 'getSecretsRequest' for device {}", request.getDeviceId());

        log.trace(request.toString());

        try {
            GetSecretsResponse response = new GetSecretsResponse();

            SecretTypes soapSecretTypes = request.getSecretTypes();

            if (soapSecretTypes == null) {
                throw new TechnicalException("Missing input: secret types");
            }

            List<SecretType> secretTypeList = converter.convertToSecretTypes(soapSecretTypes);
            List<TypedSecret> typedSecrets = secretManagementService.retrieveSecrets(request.getDeviceId(),
                    secretTypeList);

            TypedSecrets soapTypedSecrets = converter.convertToSoapTypedSecrets(typedSecrets);

            response.setTypedSecrets(soapTypedSecrets);
            response.setResult(OsgpResultType.OK);

            log.trace(response.toString());

            return response;
        } catch (Exception e) {
            throw new TechnicalException(ComponentType.SHARED, e.getMessage());
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "storeSecretsRequest")
    @ResponsePayload
    public StoreSecretsResponse storeSecretsRequest(@RequestPayload StoreSecretsRequest request) throws OsgpException {

        log.info("Handling incoming SOAP request 'storeSecretsRequest' for device {}", request.getDeviceId());
        log.trace(request.toString());

        StoreSecretsResponse response = new StoreSecretsResponse();

        try {
            TypedSecrets soapTypedSecrets = request.getTypedSecrets();

            if (soapTypedSecrets == null) {
                throw new TechnicalException("Missing input: typed secrets");
            }

            List<TypedSecret> typedSecretList = converter.convertToTypedSecrets(request.getTypedSecrets());

            secretManagementService.storeSecrets(request.getDeviceId(), typedSecretList);

            response.setResult(OsgpResultType.OK);

            log.trace(response.toString());

            return response;
        } catch (Exception e) {
            throw new TechnicalException(ComponentType.SHARED, e.getMessage());
        }
    }

}
