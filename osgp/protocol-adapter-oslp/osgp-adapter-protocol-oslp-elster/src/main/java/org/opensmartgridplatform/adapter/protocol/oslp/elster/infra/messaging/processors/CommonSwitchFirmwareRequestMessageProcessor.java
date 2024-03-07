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
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SwitchFirmwareDeviceRequest;
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

/** Class for processing common switch firmware request messages */
@Component("oslpCommonSwitchFirmwareRequestMessageProcessor")
public class CommonSwitchFirmwareRequestMessageProcessor extends DeviceRequestMessageProcessor
    implements OslpEnvelopeProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(CommonSwitchFirmwareRequestMessageProcessor.class);

  public CommonSwitchFirmwareRequestMessageProcessor() {
    super(MessageType.SWITCH_FIRMWARE);
  }

  @Override
  public void processMessage(final ObjectMessage message) {
    LOGGER.debug("Processing common switch firmware message");

    final MessageMetadata messageMetadata;
    final String version;
    try {
      messageMetadata = MessageMetadata.fromMessage(message);
      version = (String) message.getObject();
    } catch (final JMSException e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      return;
    }

    this.printDomainInfo(
        messageMetadata.getMessageType(),
        messageMetadata.getDomain(),
        messageMetadata.getDomainVersion());

    final SwitchFirmwareDeviceRequest deviceRequest =
        new SwitchFirmwareDeviceRequest(
            DeviceRequest.newBuilder().messageMetaData(messageMetadata), version);

    this.deviceService.switchFirmware(deviceRequest);
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
            CommonSwitchFirmwareRequestMessageProcessor.this.handleEmptyDeviceResponse(
                deviceResponse,
                CommonSwitchFirmwareRequestMessageProcessor.this.responseMessageSender,
                domain,
                domainVersion,
                messageType,
                retryCount);
          }

          @Override
          public void handleException(final Throwable t, final DeviceResponse deviceResponse) {
            CommonSwitchFirmwareRequestMessageProcessor.this.handleUnableToConnectDeviceResponse(
                deviceResponse, t, domain, domainVersion, messageType, isScheduled, retryCount);
          }
        };

    final DeviceRequest deviceRequest =
        new DeviceRequest(
            organisationIdentification, deviceIdentification, correlationUid, messagePriority);

    try {
      this.deviceService.doSwitchFirmware(
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
