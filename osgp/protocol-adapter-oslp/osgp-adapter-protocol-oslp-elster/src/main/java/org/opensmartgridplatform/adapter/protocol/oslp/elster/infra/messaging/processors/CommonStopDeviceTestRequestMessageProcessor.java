//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.processors;

import java.io.IOException;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponseHandler;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OslpEnvelopeProcessor;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.oslp.SignedOslpEnvelopeDto;
import org.opensmartgridplatform.oslp.UnsignedOslpEnvelopeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Class for processing common stop device test request message */
@Component("oslpCommonStopDeviceTestRequestMessageProcessor")
public class CommonStopDeviceTestRequestMessageProcessor extends DeviceRequestMessageProcessor
    implements OslpEnvelopeProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(CommonStopDeviceTestRequestMessageProcessor.class);

  public CommonStopDeviceTestRequestMessageProcessor() {
    super(MessageType.STOP_SELF_TEST);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    LOGGER.debug("Processing common stop device test request message");

    MessageMetadata messageMetadata;
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

    this.deviceService.stopSelfTest(deviceRequest);
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
            CommonStopDeviceTestRequestMessageProcessor.this.handleEmptyDeviceResponse(
                deviceResponse,
                CommonStopDeviceTestRequestMessageProcessor.this.responseMessageSender,
                domain,
                domainVersion,
                messageType,
                retryCount);
          }

          @Override
          public void handleException(final Throwable t, final DeviceResponse deviceResponse) {
            CommonStopDeviceTestRequestMessageProcessor.this.handleUnableToConnectDeviceResponse(
                deviceResponse, t, domain, domainVersion, messageType, isScheduled, retryCount);
          }
        };

    final DeviceRequest deviceRequest =
        new DeviceRequest(
            organisationIdentification, deviceIdentification, correlationUid, messagePriority);

    try {
      this.deviceService.doStopSelfTest(
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
