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
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.FirmwareLocation;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.UpdateFirmwareDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OslpEnvelopeProcessor;
import org.opensmartgridplatform.dto.valueobjects.FirmwareUpdateMessageDataContainer;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.oslp.SignedOslpEnvelopeDto;
import org.opensmartgridplatform.oslp.UnsignedOslpEnvelopeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Class for processing common update firmware request messages */
@Component("oslpCommonUpdateFirmwareRequestMessageProcessor")
public class CommonUpdateFirmwareRequestMessageProcessor extends DeviceRequestMessageProcessor
    implements OslpEnvelopeProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(CommonUpdateFirmwareRequestMessageProcessor.class);

  @Autowired private FirmwareLocation firmwareLocation;

  public CommonUpdateFirmwareRequestMessageProcessor() {
    super(MessageType.UPDATE_FIRMWARE);
  }

  // IDEA: the FirmwareLocation class in domain and dto can/must be deleted!
  // Or, this
  // setup has to be changed in order to reuse the FirmwareLocation class in
  // the domain!!

  @Override
  public void processMessage(final ObjectMessage message) {
    LOGGER.debug("Processing common update firmware request message");

    final MessageMetadata messageMetadata;
    final FirmwareUpdateMessageDataContainer firmwareUpdateMessageDataContainer;
    try {
      messageMetadata = MessageMetadata.fromMessage(message);
      firmwareUpdateMessageDataContainer = (FirmwareUpdateMessageDataContainer) message.getObject();
    } catch (final JMSException e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      return;
    }

    try {
      final String firmwareIdentification = firmwareUpdateMessageDataContainer.getFirmwareUrl();

      this.printDomainInfo(
          messageMetadata.getMessageType(),
          messageMetadata.getDomain(),
          messageMetadata.getDomainVersion());

      final UpdateFirmwareDeviceRequest deviceRequest =
          new UpdateFirmwareDeviceRequest(
              DeviceRequest.newBuilder().messageMetaData(messageMetadata),
              this.firmwareLocation.getDomain(),
              this.firmwareLocation.getFullPath(firmwareIdentification));

      this.deviceService.updateFirmware(deviceRequest);
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
            CommonUpdateFirmwareRequestMessageProcessor.this.handleEmptyDeviceResponse(
                deviceResponse,
                CommonUpdateFirmwareRequestMessageProcessor.this.responseMessageSender,
                domain,
                domainVersion,
                messageType,
                retryCount);
          }

          @Override
          public void handleException(final Throwable t, final DeviceResponse deviceResponse) {
            CommonUpdateFirmwareRequestMessageProcessor.this.handleUnableToConnectDeviceResponse(
                deviceResponse, t, domain, domainVersion, messageType, isScheduled, retryCount);
          }
        };

    final DeviceRequest deviceRequest =
        new DeviceRequest(
            organisationIdentification, deviceIdentification, correlationUid, messagePriority);

    try {
      this.deviceService.doUpdateFirmware(
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

  // Method added for testing, make this a protected method if needed
  public void setFirmwareLocation(final FirmwareLocation firmwareLocation) {
    this.firmwareLocation = firmwareLocation;
  }
}
