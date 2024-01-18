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
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SetScheduleDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OslpEnvelopeProcessor;
import org.opensmartgridplatform.dto.valueobjects.RelayTypeDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleMessageDataContainerDto;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.oslp.SignedOslpEnvelopeDto;
import org.opensmartgridplatform.oslp.UnsignedOslpEnvelopeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Class for processing tariff switching set schedule request messages */
@Component("oslpTariffSwitchingSetScheduleRequestMessageProcessor")
public class TariffSwitchingSetScheduleRequestMessageProcessor extends DeviceRequestMessageProcessor
    implements OslpEnvelopeProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(TariffSwitchingSetScheduleRequestMessageProcessor.class);

  public TariffSwitchingSetScheduleRequestMessageProcessor() {
    super(MessageType.SET_TARIFF_SCHEDULE);
  }

  @Override
  public void processMessage(final ObjectMessage message) {
    LOGGER.debug("Processing tariff switching set schedule request message");

    final MessageMetadata messageMetadata;
    final ScheduleDto schedule;
    try {
      messageMetadata = MessageMetadata.fromMessage(message);
      schedule = (ScheduleDto) message.getObject();
    } catch (final JMSException e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      return;
    }

    try {
      final ScheduleMessageDataContainerDto scheduleMessageDataContainer =
          new ScheduleMessageDataContainerDto.Builder(schedule).build();

      this.printDomainInfo(
          messageMetadata.getMessageType(),
          messageMetadata.getDomain(),
          messageMetadata.getDomainVersion());

      final SetScheduleDeviceRequest deviceRequest =
          new SetScheduleDeviceRequest(
              DeviceRequest.newBuilder().messageMetaData(messageMetadata),
              scheduleMessageDataContainer,
              RelayTypeDto.TARIFF);

      this.deviceService.setSchedule(deviceRequest);
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
    final ScheduleMessageDataContainerDto dataContainer =
        (ScheduleMessageDataContainerDto) unsignedOslpEnvelopeDto.getExtraData();

    final DeviceResponseHandler deviceResponseHandler =
        new DeviceResponseHandler() {

          @Override
          public void handleResponse(final DeviceResponse deviceResponse) {
            TariffSwitchingSetScheduleRequestMessageProcessor.this.handleEmptyDeviceResponse(
                deviceResponse,
                TariffSwitchingSetScheduleRequestMessageProcessor.this.responseMessageSender,
                domain,
                domainVersion,
                messageType,
                retryCount);
          }

          @Override
          public void handleException(final Throwable t, final DeviceResponse deviceResponse) {
            TariffSwitchingSetScheduleRequestMessageProcessor.this
                .handleUnableToConnectDeviceResponse(
                    deviceResponse, t, domain, domainVersion, messageType, isScheduled, retryCount);
          }
        };

    final DeviceRequest.Builder builder =
        DeviceRequest.newBuilder()
            .organisationIdentification(organisationIdentification)
            .deviceIdentification(deviceIdentification)
            .correlationUid(correlationUid)
            .domain(domain)
            .domainVersion(domainVersion)
            .messageType(messageType)
            .messagePriority(messagePriority)
            .ipAddress(ipAddress)
            .retryCount(retryCount)
            .isScheduled(isScheduled);

    final SetScheduleDeviceRequest deviceRequest =
        new SetScheduleDeviceRequest(builder, dataContainer, RelayTypeDto.TARIFF);

    try {
      this.deviceService.doSetSchedule(
          oslpEnvelope,
          deviceRequest,
          deviceResponseHandler,
          ipAddress,
          domain,
          domainVersion,
          messageType,
          messagePriority,
          retryCount,
          isScheduled,
          dataContainer.getPageInfo());
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
