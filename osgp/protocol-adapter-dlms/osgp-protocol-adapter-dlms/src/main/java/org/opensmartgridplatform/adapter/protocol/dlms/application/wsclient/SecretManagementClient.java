// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.wsclient;

import javax.xml.namespace.QName;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
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
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;

/** SOAP Client for SecretManagement */
@Slf4j
@Component
public class SecretManagementClient {

  private static final String NAMESPACE_URI =
      "http://www.opensmartgridplatform" + ".org/schemas/security/secretmanagement";
  private static final String CORRELATION_UID = "correlationUid";
  private final WebServiceTemplate webServiceTemplate;

  SecretManagementClient(final WebServiceTemplate webServiceTemplate) {
    this.webServiceTemplate = webServiceTemplate;
  }

  private WebServiceMessageCallback createCorrelationHeaderCallback(
      final MessageMetadata messageMetadata) {
    return message -> {
      final SoapMessage soapMessage = (SoapMessage) message;
      final SoapHeader header = soapMessage.getSoapHeader();
      header
          .addHeaderElement(new QName(NAMESPACE_URI, CORRELATION_UID))
          .setText(messageMetadata.getCorrelationUid());
    };
  }

  public GetSecretsResponse getSecretsRequest(
      final MessageMetadata messageMetadata, final GetSecretsRequest request) {

    log.info(
        "Calling SecretManagement.getSecretsRequest over SOAP for device {}",
        request.getDeviceId());

    return (GetSecretsResponse)
        this.webServiceTemplate.marshalSendAndReceive(
            request, this.createCorrelationHeaderCallback(messageMetadata));
  }

  public GetNewSecretsResponse getNewSecretsRequest(
      final MessageMetadata messageMetadata, final GetNewSecretsRequest request) {

    log.info(
        "Calling SecretManagement.getNewSecretsRequest over SOAP for device {}",
        request.getDeviceId());

    return (GetNewSecretsResponse)
        this.webServiceTemplate.marshalSendAndReceive(
            request, this.createCorrelationHeaderCallback(messageMetadata));
  }

  public StoreSecretsResponse storeSecretsRequest(
      final MessageMetadata messageMetadata, final StoreSecretsRequest request) {
    log.info(
        "Calling SecretManagement.storeSecretsRequest over SOAP for device {}",
        request.getDeviceId());

    return (StoreSecretsResponse)
        this.webServiceTemplate.marshalSendAndReceive(
            request, this.createCorrelationHeaderCallback(messageMetadata));
  }

  public ActivateSecretsResponse activateSecretsRequest(
      final MessageMetadata messageMetadata, final ActivateSecretsRequest request) {
    log.info(
        "Calling SecretManagement.activateSecretsRequest over SOAP for device {}",
        request.getDeviceId());

    return (ActivateSecretsResponse)
        this.webServiceTemplate.marshalSendAndReceive(
            request, this.createCorrelationHeaderCallback(messageMetadata));
  }

  public HasNewSecretResponse hasNewSecretRequest(
      final MessageMetadata messageMetadata, final HasNewSecretRequest request) {
    log.info(
        "Calling SecretManagement.hasNewSecretsRequest over SOAP for device {}",
        request.getDeviceId());

    return (HasNewSecretResponse)
        this.webServiceTemplate.marshalSendAndReceive(
            request, this.createCorrelationHeaderCallback(messageMetadata));
  }

  public GenerateAndStoreSecretsResponse generateAndStoreSecrets(
      final MessageMetadata messageMetadata, final GenerateAndStoreSecretsRequest request) {
    log.info(
        "Calling SecretManagement.generateAndStoreSecrets over SOAP for device {}",
        request.getDeviceId());

    return (GenerateAndStoreSecretsResponse)
        this.webServiceTemplate.marshalSendAndReceive(
            request, this.createCorrelationHeaderCallback(messageMetadata));
  }
}
