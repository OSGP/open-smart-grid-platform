// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.processors;

import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import java.io.IOException;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponseHandler;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.responses.GetConfigurationDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.MessageMetadataFactory;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OslpEnvelopeProcessor;
import org.opensmartgridplatform.dto.valueobjects.ConfigurationDto;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.oslp.SignedOslpEnvelopeDto;
import org.opensmartgridplatform.oslp.UnsignedOslpEnvelopeDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Class for processing common get configuration request messages */
@Component("oslpCommonGetConfigurationRequestMessageProcessor")
public class CommonGetConfigurationRequestMessageProcessor extends DeviceRequestMessageProcessor
    implements OslpEnvelopeProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(CommonGetConfigurationRequestMessageProcessor.class);

  public CommonGetConfigurationRequestMessageProcessor() {
    super(MessageType.GET_CONFIGURATION);
  }

  @Override
  public void processMessage(final ObjectMessage message) {
    LOGGER.debug("Processing common get configuration message");

    final MessageMetadata messageMetadata;
    try {
      messageMetadata = MessageMetadata.fromMessage(message);
    } catch (final JMSException e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      return;
    }

    this.printDomainInfo(
        messageMetadata.getMessageType(),
        messageMetadata.getDomain(),
        messageMetadata.getDomainVersion());

    final DeviceRequest deviceRequest =
        DeviceRequest.newBuilder().messageMetaData(messageMetadata).build();

    this.deviceService.getConfiguration(deviceRequest);
  }

  @Override
  public void processSignedOslpEnvelope(
      final String deviceIdentification, final SignedOslpEnvelopeDto signedOslpEnvelopeDto) {

    final UnsignedOslpEnvelopeDto unsignedOslpEnvelopeDto =
        signedOslpEnvelopeDto.getUnsignedOslpEnvelopeDto();
    final OslpEnvelope oslpEnvelope = signedOslpEnvelopeDto.getOslpEnvelope();
    final String correlationUid = unsignedOslpEnvelopeDto.getCorrelationUid();
    final String organisationIdentification =
        unsignedOslpEnvelopeDto.getOrganisationIdentification();
    final String domain = unsignedOslpEnvelopeDto.getDomain();
    final String domainVersion = unsignedOslpEnvelopeDto.getDomainVersion();
    final String messageType = unsignedOslpEnvelopeDto.getMessageType();
    final int messagePriority = unsignedOslpEnvelopeDto.getMessagePriority();
    final String ipAddress = unsignedOslpEnvelopeDto.getIpAddress();
    final int retryCount = unsignedOslpEnvelopeDto.getRetryCount();
    final boolean isScheduled = unsignedOslpEnvelopeDto.isScheduled();

    final DeviceResponseHandler deviceResponseHandler =
        new DeviceResponseHandler() {

          @Override
          public void handleResponse(final DeviceResponse deviceResponse) {
            CommonGetConfigurationRequestMessageProcessor.this.handleGetConfigurationDeviceResponse(
                deviceResponse,
                CommonGetConfigurationRequestMessageProcessor.this.responseMessageSender,
                domain,
                domainVersion,
                messageType,
                retryCount);
          }

          @Override
          public void handleException(final Throwable t, final DeviceResponse deviceResponse) {
            CommonGetConfigurationRequestMessageProcessor.this.handleUnableToConnectDeviceResponse(
                deviceResponse, t, domain, domainVersion, messageType, isScheduled, retryCount);
          }
        };

    final DeviceRequest deviceRequest =
        new DeviceRequest(
            organisationIdentification, deviceIdentification, correlationUid, messagePriority);

    try {
      this.deviceService.doGetConfiguration(
          oslpEnvelope, deviceRequest, deviceResponseHandler, ipAddress);
    } catch (final IOException e) {
      this.handleError(
          e,
          correlationUid,
          organisationIdentification,
          deviceIdentification,
          domain,
          domainVersion,
          messageType,
          messagePriority,
          retryCount);
    }
  }

  private void handleGetConfigurationDeviceResponse(
      final DeviceResponse deviceResponse,
      final ResponseMessageSender responseMessageSender,
      final String domain,
      final String domainVersion,
      final String messageType,
      final int retryCount) {

    ResponseMessageResultType result = ResponseMessageResultType.OK;
    OsgpException osgpException = null;
    ConfigurationDto configuration = null;

    try {
      final GetConfigurationDeviceResponse response =
          (GetConfigurationDeviceResponse) deviceResponse;
      this.deviceResponseService.handleDeviceMessageStatus(response.getStatus());
      configuration = response.getConfiguration();
    } catch (final Exception e) {
      LOGGER.error("Device Response Exception", e);
      result = ResponseMessageResultType.NOT_OK;
      osgpException =
          new TechnicalException(
              ComponentType.UNKNOWN, "Exception occurred while getting device configuration", e);
    }

    final ProtocolResponseMessage responseMessage =
        ProtocolResponseMessage.newBuilder()
            .messageMetadata(
                MessageMetadataFactory.from(deviceResponse, messageType)
                    .builder()
                    .withDomain(domain)
                    .withDomainVersion(domainVersion)
                    .withRetryCount(retryCount)
                    .build())
            .result(result)
            .osgpException(osgpException)
            .dataObject(configuration)
            .build();

    responseMessageSender.send(responseMessage);
  }
}
