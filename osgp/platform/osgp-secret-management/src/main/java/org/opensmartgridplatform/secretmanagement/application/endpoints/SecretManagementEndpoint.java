/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.endpoints;

import java.io.ByteArrayOutputStream;
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

  private static final String NAMESPACE_URI =
      "http://www.opensmartgridplatform.org/schemas/security/secretmanagement";
  private static final String STR_MISSING_SECRET_TYPES = "Missing input: secret types";
  private static final String STR_MISSING_TYPED_SECRETS = "Missing input: typed secrets";

  private final SecretManagementService secretManagementService;
  private final SoapEndpointDataTypeConverter converter;

  public SecretManagementEndpoint(
      final SecretManagementService secretManagementService,
      final SoapEndpointDataTypeConverter converter) {
    this.secretManagementService = secretManagementService;
    this.converter = converter;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSecretsRequest")
  @ResponsePayload
  public GetSecretsResponse getSecretsRequest(@RequestPayload final GetSecretsRequest request)
      throws OsgpException {
    log.info(
        "Handling incoming SOAP request 'getSecretsRequest' for device {}", request.getDeviceId());
    if (log.isDebugEnabled()) {
      log.debug(this.requestToString(request));
    }
    final GetSecretsResponse response = new GetSecretsResponse();
    final SecretTypes soapSecretTypes = request.getSecretTypes();
    if (soapSecretTypes == null) {
      throw new TechnicalException(STR_MISSING_SECRET_TYPES);
    }
    final List<SecretType> secretTypeList = this.converter.convertToSecretTypes(soapSecretTypes);
    final List<TypedSecret> typedSecrets =
        this.secretManagementService.retrieveSecrets(request.getDeviceId(), secretTypeList);
    final TypedSecrets soapTypedSecrets = this.converter.convertToSoapTypedSecrets(typedSecrets);
    response.setTypedSecrets(soapTypedSecrets);
    response.setResult(OsgpResultType.OK);
    return response;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getNewSecretsRequest")
  @ResponsePayload
  public GetNewSecretsResponse getNewSecretsRequest(
      @RequestPayload final GetNewSecretsRequest request) throws OsgpException {
    log.info(
        "Handling incoming SOAP request 'getSecretsRequest' for device {}", request.getDeviceId());
    if (log.isDebugEnabled()) {
      log.debug(this.requestToString(request));
    }
    final GetNewSecretsResponse response = new GetNewSecretsResponse();
    final SecretTypes soapSecretTypes = request.getSecretTypes();
    if (soapSecretTypes == null) {
      throw new TechnicalException(STR_MISSING_SECRET_TYPES);
    }
    final List<SecretType> secretTypeList = this.converter.convertToSecretTypes(soapSecretTypes);
    final List<TypedSecret> typedSecrets =
        this.secretManagementService.retrieveNewSecrets(request.getDeviceId(), secretTypeList);
    final TypedSecrets soapTypedSecrets = this.converter.convertToSoapTypedSecrets(typedSecrets);
    response.setTypedSecrets(soapTypedSecrets);
    response.setResult(OsgpResultType.OK);
    return response;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "storeSecretsRequest")
  @ResponsePayload
  public StoreSecretsResponse storeSecretsRequest(@RequestPayload final StoreSecretsRequest request)
      throws OsgpException {
    log.info(
        "Handling incoming SOAP request 'storeSecretsRequest' for device {}",
        request.getDeviceId());
    if (log.isDebugEnabled()) {
      log.debug(this.requestToString(request));
    }
    final StoreSecretsResponse response = new StoreSecretsResponse();
    final TypedSecrets soapTypedSecrets = request.getTypedSecrets();
    if (soapTypedSecrets == null) {
      throw new TechnicalException(STR_MISSING_TYPED_SECRETS);
    }
    final List<TypedSecret> typedSecretList =
        this.converter.convertToTypedSecrets(soapTypedSecrets);
    this.secretManagementService.storeSecrets(request.getDeviceId(), typedSecretList);
    response.setResult(OsgpResultType.OK);
    return response;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "generateAndStoreSecretsRequest")
  @ResponsePayload
  public GenerateAndStoreSecretsResponse generateAndStoreSecretsRequest(
      @RequestPayload final GenerateAndStoreSecretsRequest request) throws OsgpException {
    log.info(
        "Handling incoming SOAP request 'generateAndStoreSecretsRequest' for device {}",
        request.getDeviceId());
    if (log.isDebugEnabled()) {
      log.debug(this.requestToString(request));
    }
    final GenerateAndStoreSecretsResponse response = new GenerateAndStoreSecretsResponse();
    final SecretTypes soapSecretTypes = request.getSecretTypes();
    if (soapSecretTypes == null) {
      throw new TechnicalException(STR_MISSING_SECRET_TYPES);
    }
    final List<SecretType> secretTypeList = this.converter.convertToSecretTypes(soapSecretTypes);
    final List<TypedSecret> typedSecretList =
        this.secretManagementService.generateAndStoreSecrets(request.getDeviceId(), secretTypeList);
    response.setResult(OsgpResultType.OK);
    response.setTypedSecrets(this.converter.convertToSoapTypedSecrets(typedSecretList));
    return response;
  }

  private <T> String requestToString(final T request) {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      final JAXBContext ctx = JAXBContext.newInstance(request.getClass());
      final Marshaller marshaller = ctx.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(request, baos);
    } catch (final JAXBException e) {
      final String logFormat = "Could not serialize request of type %s";
      log.error(String.format(logFormat, request.getClass()), e);
    }
    return baos.toString();
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "activateSecretsRequest")
  @ResponsePayload
  public ActivateSecretsResponse activateSecretsRequest(
      @RequestPayload final ActivateSecretsRequest request) throws OsgpException {
    log.info(
        "Handling incoming SOAP request 'activateSecretRequest' for device {}",
        request.getDeviceId());
    if (log.isDebugEnabled()) {
      log.debug(this.requestToString(request));
    }
    final ActivateSecretsResponse response = new ActivateSecretsResponse();
    final SecretTypes soapSecretTypes = request.getSecretTypes();
    if (soapSecretTypes == null) {
      throw new TechnicalException(STR_MISSING_SECRET_TYPES);
    }
    try {
      this.secretManagementService.activateNewSecrets(
          request.getDeviceId(), this.converter.convertToSecretTypes(soapSecretTypes));
      response.setResult(OsgpResultType.OK);
    } catch (final RuntimeException rte) {
      log.error("Could not activate new secrets: " + rte.toString());
      throw new TechnicalException("Could not activate new secrets", rte);
    }
    return response;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "hasNewSecretRequest")
  @ResponsePayload
  public HasNewSecretResponse hasNewSecretRequest(@RequestPayload final HasNewSecretRequest request)
      throws OsgpException {
    log.info(
        "Handling incoming SOAP request 'hasNewSecretRequest' for device {}",
        request.getDeviceId());
    if (log.isDebugEnabled()) {
      log.debug(this.requestToString(request));
    }
    final HasNewSecretResponse response = new HasNewSecretResponse();
    final SecretType type = this.converter.convertToSecretType(request.getSecretType());
    final boolean result = this.secretManagementService.hasNewSecret(request.getDeviceId(), type);
    response.setHasNewSecret(result);
    return response;
  }
}
