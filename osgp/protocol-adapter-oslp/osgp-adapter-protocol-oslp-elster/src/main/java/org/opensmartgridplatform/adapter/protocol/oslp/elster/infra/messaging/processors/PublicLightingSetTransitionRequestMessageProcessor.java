// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.processors;

import java.io.IOException;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponseHandler;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SetTransitionDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OslpEnvelopeProcessor;
import org.opensmartgridplatform.dto.valueobjects.TransitionMessageDataContainerDto;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.oslp.SignedOslpEnvelopeDto;
import org.opensmartgridplatform.oslp.UnsignedOslpEnvelopeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Class for processing public lighting set transition request messages */
@Component("oslpPublicLightingSetTransitionRequestMessageProcessor")
public class PublicLightingSetTransitionRequestMessageProcessor
    extends DeviceRequestMessageProcessor implements OslpEnvelopeProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(PublicLightingSetTransitionRequestMessageProcessor.class);

  public PublicLightingSetTransitionRequestMessageProcessor() {
    super(MessageType.SET_TRANSITION);
  }

  @Override
  public void processMessage(final ObjectMessage message) {
    LOGGER.debug("Processing public lighting set transition request message");

    MessageMetadata messageMetadata;
    TransitionMessageDataContainerDto transitionMessageDataContainer;
    try {
      messageMetadata = MessageMetadata.fromMessage(message);
      transitionMessageDataContainer = (TransitionMessageDataContainerDto) message.getObject();
    } catch (final JMSException e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      return;
    }

    try {
      this.printDomainInfo(
          messageMetadata.getMessageType(),
          messageMetadata.getDomain(),
          messageMetadata.getDomainVersion());

      final SetTransitionDeviceRequest deviceRequest =
          new SetTransitionDeviceRequest(
              DeviceRequest.newBuilder().messageMetaData(messageMetadata),
              transitionMessageDataContainer);

      this.deviceService.setTransition(deviceRequest);
    } catch (final RuntimeException e) {
      this.handleError(e, messageMetadata);
    }
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
            PublicLightingSetTransitionRequestMessageProcessor.this.handleEmptyDeviceResponse(
                deviceResponse,
                PublicLightingSetTransitionRequestMessageProcessor.this.responseMessageSender,
                domain,
                domainVersion,
                messageType,
                retryCount);
          }

          @Override
          public void handleException(final Throwable t, final DeviceResponse deviceResponse) {
            PublicLightingSetTransitionRequestMessageProcessor.this
                .handleUnableToConnectDeviceResponse(
                    deviceResponse, t, domain, domainVersion, messageType, isScheduled, retryCount);
          }
        };

    final DeviceRequest deviceRequest =
        new DeviceRequest(
            organisationIdentification, deviceIdentification, correlationUid, messagePriority);

    try {
      this.deviceService.doSetTransition(
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
}
