// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.wsclient;

import static org.opensmartgridplatform.adapter.protocol.dlms.application.metrics.ProtocolAdapterMetrics.METRIC_REQUEST_TIMER_PREFIX;
import static org.opensmartgridplatform.adapter.protocol.dlms.application.metrics.ProtocolAdapterMetrics.TAG_NR_OF_KEYS;

import io.micrometer.core.instrument.Timer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.xml.namespace.QName;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.protocol.dlms.application.metrics.ProtocolAdapterMetrics;
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

  private final WebServiceTemplate webServiceTemplate;
  private final ProtocolAdapterMetrics protocolAdapterMetrics;

  private static final String NAMESPACE_URI =
      "http://www.opensmartgridplatform" + ".org/schemas/security/secretmanagement";
  private static final String CORRELATION_UID = "correlationUid";

  public SecretManagementClient(
      final WebServiceTemplate webServiceTemplate,
      final ProtocolAdapterMetrics protocolAdapterMetrics) {
    this.webServiceTemplate = webServiceTemplate;
    this.protocolAdapterMetrics = protocolAdapterMetrics;
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

  @TrackExecutionTime(timerName = "GetSecrets")
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

    final Timer timer =
        this.createTimer("GetNewSecrets", request.getSecretTypes().getSecretType().size());
    final long starttime = System.currentTimeMillis();

    final GetNewSecretsResponse response =
        (GetNewSecretsResponse)
            this.webServiceTemplate.marshalSendAndReceive(
                request, this.createCorrelationHeaderCallback(messageMetadata));

    this.recordTimer(timer, starttime);

    return response;
  }

  public StoreSecretsResponse storeSecretsRequest(
      final MessageMetadata messageMetadata, final StoreSecretsRequest request) {
    log.info(
        "Calling SecretManagement.storeSecretsRequest over SOAP for device {}",
        request.getDeviceId());

    final Timer timer =
        this.createTimer("StoreSecrets", request.getTypedSecrets().getTypedSecret().size());
    final long starttime = System.currentTimeMillis();

    final StoreSecretsResponse response =
        (StoreSecretsResponse)
            this.webServiceTemplate.marshalSendAndReceive(
                request, this.createCorrelationHeaderCallback(messageMetadata));

    this.recordTimer(timer, starttime);

    return response;
  }

  public ActivateSecretsResponse activateSecretsRequest(
      final MessageMetadata messageMetadata, final ActivateSecretsRequest request) {
    log.info(
        "Calling SecretManagement.activateSecretsRequest over SOAP for device {}",
        request.getDeviceId());

    final Timer timer =
        this.createTimer("ActivateSecrets", request.getSecretTypes().getSecretType().size());
    final long starttime = System.currentTimeMillis();

    final ActivateSecretsResponse response =
        (ActivateSecretsResponse)
            this.webServiceTemplate.marshalSendAndReceive(
                request, this.createCorrelationHeaderCallback(messageMetadata));

    this.recordTimer(timer, starttime);

    return response;
  }

  public HasNewSecretResponse hasNewSecretRequest(
      final MessageMetadata messageMetadata, final HasNewSecretRequest request) {
    log.info(
        "Calling SecretManagement.hasNewSecretRequest over SOAP for device {}",
        request.getDeviceId());

    final Timer timer = this.createTimer("HasNewSecret", 1);
    final long starttime = System.currentTimeMillis();

    final HasNewSecretResponse response =
        (HasNewSecretResponse)
            this.webServiceTemplate.marshalSendAndReceive(
                request, this.createCorrelationHeaderCallback(messageMetadata));

    this.recordTimer(timer, starttime);

    return response;
  }

  public GenerateAndStoreSecretsResponse generateAndStoreSecrets(
      final MessageMetadata messageMetadata, final GenerateAndStoreSecretsRequest request) {
    log.info(
        "Calling SecretManagement.generateAndStoreSecrets over SOAP for device {}",
        request.getDeviceId());

    final Timer timer =
        this.createTimer(
            "GenerateAndStoreSecrets", request.getSecretTypes().getSecretType().size());
    final long starttime = System.currentTimeMillis();

    final GenerateAndStoreSecretsResponse response =
        (GenerateAndStoreSecretsResponse)
            this.webServiceTemplate.marshalSendAndReceive(
                request, this.createCorrelationHeaderCallback(messageMetadata));

    this.recordTimer(timer, starttime);

    return response;
  }

  private Timer createTimer(final String timerName, final int nrOfKeys) {
    final Map<String, String> tags = new HashMap<>();
    tags.put(TAG_NR_OF_KEYS, String.valueOf(nrOfKeys));
    return this.protocolAdapterMetrics.createTimer(METRIC_REQUEST_TIMER_PREFIX + timerName, tags);
  }

  private void recordTimer(final Timer timer, final long starttime) {
    this.protocolAdapterMetrics.recordTimer(
        timer, System.currentTimeMillis() - starttime, TimeUnit.MILLISECONDS);
  }
}
