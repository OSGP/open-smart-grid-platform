/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.endpoints;

import java.io.ByteArrayOutputStream;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretType;
import org.opensmartgridplatform.secretmanagement.application.domain.TypedSecret;
import org.opensmartgridplatform.secretmanagement.application.services.SecretManagementService;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.ws.schema.core.secret.management.AbstractRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.AbstractResponse;
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
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.soap.server.endpoint.annotation.SoapHeader;
import org.springframework.xml.transform.StringSource;

@Endpoint
@Slf4j
public class SecretManagementEndpoint {
  @FunctionalInterface
  private interface RequestProcessor<R extends AbstractRequest, S extends AbstractResponse> {
    S processRequest(R request) throws OsgpException;
  }

  private static final String NAMESPACE_URI =
      "http://www.opensmartgridplatform.org/schemas/security/secretmanagement";
  private static final String CORRELATION_UID = "correlationUid";
  private static final String CORRELATION_HEADER = "{" + NAMESPACE_URI + "}" + CORRELATION_UID;

  private final SecretManagementService secretManagementService;
  private final SoapEndpointDataTypeConverter converter;

  public SecretManagementEndpoint(
      final SecretManagementService secretManagementService,
      final SoapEndpointDataTypeConverter converter) {
    this.secretManagementService = secretManagementService;
    this.converter = converter;
  }

  private String getCorrelationUidFromHeader(final SoapHeaderElement header) {
    return header != null ? header.getText() : null;
  }

  private void addHeaderToResponse(
      final MessageContext messageContext, final SoapHeaderElement header)
      throws TransformerException {
    if (header != null) {
      final SaajSoapMessage soapResponse = (SaajSoapMessage) messageContext.getResponse();
      final org.springframework.ws.soap.SoapHeader responseHeader = soapResponse.getSoapHeader();
      final TransformerFactory transformerFactory = TransformerFactory.newInstance();
      final String headerXml =
          String.format(
              "<%1$s xmlns=\"%2$s\">%3$s</%1$s>", CORRELATION_UID, NAMESPACE_URI, header.getText());
      final StringSource headerSource = new StringSource(headerXml);
      final Transformer transformer = transformerFactory.newTransformer();
      transformer.transform(headerSource, responseHeader.getResult());
    }
  }

  private <R extends AbstractRequest, S extends AbstractResponse> S handleRequest(
      final R request,
      final RequestProcessor<R, S> processor,
      final SoapHeaderElement header,
      final MessageContext messageContext)
      throws TransformerException, OsgpException {
    final String correlationUid = this.getCorrelationUidFromHeader(header);
    log.info(
        "[{}] Handling incoming SOAP request '{}' for device {}",
        correlationUid,
        request.getClass().getSimpleName(),
        request.getDeviceId());
    if (log.isDebugEnabled()) {
      log.debug(this.requestToString(request));
    }
    final S response = processor.processRequest(request);
    response.setResult(OsgpResultType.OK);
    this.addHeaderToResponse(messageContext, header);
    return response;
  }

  public GetSecretsResponse getSecrets(final GetSecretsRequest request) throws OsgpException {
    final GetSecretsResponse response = new GetSecretsResponse();
    final SecretTypes soapSecretTypes = request.getSecretTypes();
    final List<SecretType> secretTypeList = this.converter.convertToSecretTypes(soapSecretTypes);
    final List<TypedSecret> typedSecrets =
        this.secretManagementService.retrieveSecrets(request.getDeviceId(), secretTypeList);
    final TypedSecrets soapTypedSecrets = this.converter.convertToSoapTypedSecrets(typedSecrets);
    response.setTypedSecrets(soapTypedSecrets);
    return response;
  }

  public GetNewSecretsResponse getNewSecrets(final GetNewSecretsRequest request) {
    final GetNewSecretsResponse response = new GetNewSecretsResponse();
    final SecretTypes soapSecretTypes = request.getSecretTypes();
    final List<SecretType> secretTypeList = this.converter.convertToSecretTypes(soapSecretTypes);
    final List<TypedSecret> typedSecrets =
        this.secretManagementService.retrieveNewSecrets(request.getDeviceId(), secretTypeList);
    final TypedSecrets soapTypedSecrets = this.converter.convertToSoapTypedSecrets(typedSecrets);
    response.setTypedSecrets(soapTypedSecrets);
    return response;
  }

  public StoreSecretsResponse storeSecrets(final StoreSecretsRequest request) throws OsgpException {
    final TypedSecrets soapTypedSecrets = request.getTypedSecrets();
    final List<TypedSecret> typedSecretList =
        this.converter.convertToTypedSecrets(soapTypedSecrets);
    this.secretManagementService.storeSecrets(request.getDeviceId(), typedSecretList);
    return new StoreSecretsResponse();
  }

  public GenerateAndStoreSecretsResponse generateAndStoreSecrets(
      final GenerateAndStoreSecretsRequest request) {
    final GenerateAndStoreSecretsResponse response = new GenerateAndStoreSecretsResponse();
    final SecretTypes soapSecretTypes = request.getSecretTypes();
    final List<SecretType> secretTypeList = this.converter.convertToSecretTypes(soapSecretTypes);
    final List<TypedSecret> typedSecretList =
        this.secretManagementService.generateAndStoreSecrets(request.getDeviceId(), secretTypeList);
    response.setTypedSecrets(this.converter.convertToSoapTypedSecrets(typedSecretList));
    return response;
  }

  public ActivateSecretsResponse activateSecrets(final ActivateSecretsRequest request) {
    final SecretTypes soapSecretTypes = request.getSecretTypes();
    this.secretManagementService.activateNewSecrets(
        request.getDeviceId(), this.converter.convertToSecretTypes(soapSecretTypes));
    return new ActivateSecretsResponse();
  }

  public HasNewSecretResponse hasNewSecret(final HasNewSecretRequest request) {
    final SecretType type = this.converter.convertToSecretType(request.getSecretType());
    final boolean result = this.secretManagementService.hasNewSecret(request.getDeviceId(), type);
    final HasNewSecretResponse response = new HasNewSecretResponse();
    response.setHasNewSecret(result);
    return response;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSecretsRequest")
  @ResponsePayload
  public GetSecretsResponse getSecretsRequest(
      @RequestPayload final GetSecretsRequest request,
      @SoapHeader(CORRELATION_HEADER) final SoapHeaderElement header,
      final MessageContext messageContext)
      throws OsgpException, TransformerException {
    return this.handleRequest(request, this::getSecrets, header, messageContext);
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getNewSecretsRequest")
  @ResponsePayload
  public GetNewSecretsResponse getNewSecretsRequest(
      @RequestPayload final GetNewSecretsRequest request,
      @SoapHeader(CORRELATION_HEADER) final SoapHeaderElement header,
      final MessageContext messageContext)
      throws OsgpException, TransformerException {
    return this.handleRequest(request, this::getNewSecrets, header, messageContext);
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "storeSecretsRequest")
  @ResponsePayload
  public StoreSecretsResponse storeSecretsRequest(
      @RequestPayload final StoreSecretsRequest request,
      @SoapHeader(CORRELATION_HEADER) final SoapHeaderElement header,
      final MessageContext messageContext)
      throws OsgpException, TransformerException {
    return this.handleRequest(request, this::storeSecrets, header, messageContext);
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "generateAndStoreSecretsRequest")
  @ResponsePayload
  public GenerateAndStoreSecretsResponse generateAndStoreSecretsRequest(
      @RequestPayload final GenerateAndStoreSecretsRequest request,
      @SoapHeader(CORRELATION_HEADER) final SoapHeaderElement header,
      final MessageContext messageContext)
      throws OsgpException, TransformerException {
    return this.handleRequest(request, this::generateAndStoreSecrets, header, messageContext);
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "activateSecretsRequest")
  @ResponsePayload
  public ActivateSecretsResponse activateSecretsRequest(
      @RequestPayload final ActivateSecretsRequest request,
      @SoapHeader(CORRELATION_HEADER) final SoapHeaderElement header,
      final MessageContext messageContext)
      throws OsgpException, TransformerException {
    return this.handleRequest(request, this::activateSecrets, header, messageContext);
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "hasNewSecretRequest")
  @ResponsePayload
  public HasNewSecretResponse hasNewSecretRequest(
      @RequestPayload final HasNewSecretRequest request,
      @SoapHeader(CORRELATION_HEADER) final SoapHeaderElement header,
      final MessageContext messageContext)
      throws OsgpException, TransformerException {
    return this.handleRequest(request, this::hasNewSecret, header, messageContext);
  }

  private String requestToString(final AbstractRequest request) {
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
}
