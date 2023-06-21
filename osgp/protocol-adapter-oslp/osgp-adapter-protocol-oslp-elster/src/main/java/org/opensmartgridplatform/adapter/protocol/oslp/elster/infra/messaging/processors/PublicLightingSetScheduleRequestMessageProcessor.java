// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.processors;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.services.oslp.OslpDeviceSettingsService;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.services.oslp.PendingSetScheduleRequestService;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponseHandler;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SetScheduleDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.responses.GetConfigurationDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.entities.OslpDevice;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.entities.PendingSetScheduleRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OslpEnvelopeProcessor;
import org.opensmartgridplatform.dto.valueobjects.ConfigurationDto;
import org.opensmartgridplatform.dto.valueobjects.RelayTypeDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleMessageDataContainerDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleMessageTypeDto;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.oslp.SignedOslpEnvelopeDto;
import org.opensmartgridplatform.oslp.UnsignedOslpEnvelopeDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Class for processing public lighting set schedule request messages */
@Component("oslpPublicLightingSetScheduleRequestMessageProcessor")
public class PublicLightingSetScheduleRequestMessageProcessor extends DeviceRequestMessageProcessor
    implements OslpEnvelopeProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(PublicLightingSetScheduleRequestMessageProcessor.class);

  private static final String LOG_MESSAGE_CALL_DEVICE_SERVICE =
      "Calling DeviceService function: {} of type {} for domain: {} {}";

  @Autowired private OslpDeviceSettingsService oslpDeviceSettingsService;

  @Autowired private PendingSetScheduleRequestService pendingSetScheduleRequestService;

  @Autowired private Integer pendingSetScheduleRequestExpiresInMinutes;

  public PublicLightingSetScheduleRequestMessageProcessor() {
    super(MessageType.SET_LIGHT_SCHEDULE);
  }

  @Override
  public void processMessage(final ObjectMessage message) {
    LOGGER.debug("Processing public lighting set schedule request message");

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
      this.pendingSetScheduleRequestService.removeExpiredPendingSetScheduleRequestRecords(
          messageMetadata.getDeviceIdentification());

      final List<PendingSetScheduleRequest> pendingSetScheduleRequestList =
          this.pendingSetScheduleRequestService.getAllByDeviceIdentificationNotExpired(
              messageMetadata.getDeviceIdentification());

      if (!pendingSetScheduleRequestList.isEmpty()) {
        throw new FunctionalException(
            FunctionalExceptionType.SET_SCHEDULE_WITH_ASTRONOMICAL_OFFSETS_IN_PROGRESS,
            ComponentType.PROTOCOL_OSLP,
            new Throwable(
                String.format(
                    "A set schedule with astronomical offsets is already in progress for device %s",
                    messageMetadata.getDeviceIdentification())));
      }

      ScheduleMessageDataContainerDto.Builder builder =
          new ScheduleMessageDataContainerDto.Builder(schedule);
      if (schedule.getAstronomicalSunriseOffset() != null
          || schedule.getAstronomicalSunsetOffset() != null) {
        LOGGER.info("Set a schedule for a device with astronomical offsets");
        builder = builder.withScheduleMessageType(ScheduleMessageTypeDto.RETRIEVE_CONFIGURATION);
      }

      final ScheduleMessageDataContainerDto scheduleMessageDataContainer = builder.build();

      this.printDomainInfo(
          messageMetadata.getMessageType(),
          messageMetadata.getDomain(),
          messageMetadata.getDomainVersion());

      final SetScheduleDeviceRequest deviceRequest =
          new SetScheduleDeviceRequest(
              DeviceRequest.newBuilder().messageMetaData(messageMetadata),
              scheduleMessageDataContainer,
              RelayTypeDto.LIGHT);

      this.deviceService.setSchedule(deviceRequest);
    } catch (final RuntimeException e) {
      this.handleError(e, messageMetadata);
    } catch (final FunctionalException e) {
      this.handleFunctionalException(e, messageMetadata);
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
        new SetScheduleDeviceRequest(builder, dataContainer, RelayTypeDto.LIGHT);

    final DeviceResponseHandler deviceResponseHandler =
        new DeviceResponseHandler() {

          @Override
          public void handleResponse(final DeviceResponse deviceResponse) {
            PublicLightingSetScheduleRequestMessageProcessor.this.handleResponse(
                deviceResponse, deviceRequest);
          }

          @Override
          public void handleException(final Throwable t, final DeviceResponse deviceResponse) {
            PublicLightingSetScheduleRequestMessageProcessor.this
                .handleUnableToConnectDeviceResponse(
                    deviceResponse, t, domain, domainVersion, messageType, isScheduled, retryCount);
          }
        };

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

  private void handleResponse(
      final DeviceResponse deviceResponse, final SetScheduleDeviceRequest deviceRequest) {
    final ScheduleMessageTypeDto scheduleMessageTypeDto =
        deviceRequest.getScheduleMessageDataContainer().getScheduleMessageType();

    switch (scheduleMessageTypeDto) {
      case RETRIEVE_CONFIGURATION:
        final GetConfigurationDeviceResponse getConfigurationDeviceResponse =
            (GetConfigurationDeviceResponse) deviceResponse;
        this.handleSetScheduleGetConfigurationResponse(
            deviceRequest, getConfigurationDeviceResponse);
        break;
      case SET_ASTRONOMICAL_OFFSETS:
        this.handleSetScheduleAstronomicalOffsetsResponse(deviceRequest);
        break;
      case SET_REBOOT:
        this.handleSetScheduleSetRebootResponse(deviceRequest);
        break;
      case SET_SCHEDULE:
      default:
        this.handleEmptyDeviceResponse(
            deviceResponse,
            this.responseMessageSender,
            deviceRequest.getDomain(),
            deviceRequest.getDomainVersion(),
            deviceRequest.getMessageType(),
            deviceRequest.getRetryCount());
    }
  }

  private void handleSetScheduleGetConfigurationResponse(
      final SetScheduleDeviceRequest deviceRequest,
      final GetConfigurationDeviceResponse deviceResponse) {
    // Configuration is retrieved, so now continue with setting the
    // astronomical offsets
    LOGGER.info(
        LOG_MESSAGE_CALL_DEVICE_SERVICE,
        deviceRequest.getMessageType(),
        ScheduleMessageTypeDto.SET_ASTRONOMICAL_OFFSETS,
        deviceRequest.getDomain(),
        deviceRequest.getDomainVersion());

    final ScheduleMessageTypeDto nextRequest =
        this.determineNextRequest(deviceRequest, deviceResponse);
    LOGGER.info("Request after getConfiguration: {}", nextRequest);

    final ScheduleMessageDataContainerDto dataContainer =
        new ScheduleMessageDataContainerDto.Builder(
                deviceRequest.getScheduleMessageDataContainer().getSchedule())
            .withConfiguration(deviceResponse.getConfiguration())
            .withScheduleMessageType(nextRequest)
            .build();

    final SetScheduleDeviceRequest newDeviceRequest =
        new SetScheduleDeviceRequest(
            createDeviceRequestBuilder(deviceRequest), dataContainer, RelayTypeDto.LIGHT);

    this.deviceService.setSchedule(newDeviceRequest);
  }

  private ScheduleMessageTypeDto determineNextRequest(
      final SetScheduleDeviceRequest deviceRequest,
      final GetConfigurationDeviceResponse deviceResponse) {
    final int requestedSunriseOffset =
        deviceRequest
            .getScheduleMessageDataContainer()
            .getSchedule()
            .getAstronomicalSunriseOffset();
    final int requestedSunsetOffset =
        deviceRequest.getScheduleMessageDataContainer().getSchedule().getAstronomicalSunsetOffset();
    final ConfigurationDto configurationInDevice = deviceResponse.getConfiguration();

    LOGGER.info(
        "Requested astroGateSunRiseOffset {},  astroGateSunRiseOffset in device {}, Requested astroGateSunSetOffset {},  astroGateSunSetOffset in device {}, ",
        requestedSunriseOffset,
        configurationInDevice.getAstroGateSunRiseOffset(),
        requestedSunsetOffset,
        configurationInDevice.getAstroGateSunSetOffset());

    if (requestedSunriseOffset != configurationInDevice.getAstroGateSunRiseOffset()
        || requestedSunsetOffset != configurationInDevice.getAstroGateSunSetOffset()) {
      return ScheduleMessageTypeDto.SET_ASTRONOMICAL_OFFSETS;
    }

    return ScheduleMessageTypeDto.SET_SCHEDULE;
  }

  private void handleSetScheduleAstronomicalOffsetsResponse(
      final SetScheduleDeviceRequest deviceRequest) {
    // Configuration / Astronomical offsets are set , so now continue with
    // rebooting the device

    LOGGER.info(
        LOG_MESSAGE_CALL_DEVICE_SERVICE,
        deviceRequest.getMessageType(),
        ScheduleMessageTypeDto.SET_ASTRONOMICAL_OFFSETS,
        deviceRequest.getDomain(),
        deviceRequest.getDomainVersion());

    final ScheduleMessageDataContainerDto dataContainer =
        new ScheduleMessageDataContainerDto.Builder(
                deviceRequest.getScheduleMessageDataContainer().getSchedule())
            .withScheduleMessageType(ScheduleMessageTypeDto.SET_REBOOT)
            .build();

    final SetScheduleDeviceRequest newDeviceRequest =
        new SetScheduleDeviceRequest(
            createDeviceRequestBuilder(deviceRequest), dataContainer, RelayTypeDto.LIGHT);

    this.deviceService.setSchedule(newDeviceRequest);
  }

  private void handleSetScheduleSetRebootResponse(final SetScheduleDeviceRequest deviceRequest) {

    // The device will reboot now.
    // At this point we will need to save the current state to the
    // pending_set_schedule_request table

    LOGGER.info(
        LOG_MESSAGE_CALL_DEVICE_SERVICE,
        deviceRequest.getMessageType(),
        ScheduleMessageTypeDto.SET_SCHEDULE,
        deviceRequest.getDomain(),
        deviceRequest.getDomainVersion());

    final ScheduleMessageDataContainerDto dataContainer =
        new ScheduleMessageDataContainerDto.Builder(
                deviceRequest.getScheduleMessageDataContainer().getSchedule())
            .withScheduleMessageType(ScheduleMessageTypeDto.SET_SCHEDULE)
            .build();

    final String deviceIdentification = deviceRequest.getDeviceIdentification();
    final OslpDevice oslpDevice =
        this.oslpDeviceSettingsService.getDeviceByDeviceIdentification(deviceIdentification);
    final String deviceUid = oslpDevice.getDeviceUid();

    final Date expireDateTime =
        Date.from(
            ZonedDateTime.now()
                .plusMinutes(this.pendingSetScheduleRequestExpiresInMinutes)
                .toInstant());

    final PendingSetScheduleRequest pendingSetScheduleRequest =
        PendingSetScheduleRequest.builder()
            .deviceIdentification(deviceIdentification)
            .deviceUid(deviceUid)
            .scheduleMessageDataContainerDto(dataContainer)
            .deviceRequest(deviceRequest)
            .expiredAt(expireDateTime)
            .build();

    this.pendingSetScheduleRequestService.add(pendingSetScheduleRequest);
  }

  private static DeviceRequest.Builder createDeviceRequestBuilder(
      final DeviceRequest deviceRequest) {
    return DeviceRequest.newBuilder()
        .organisationIdentification(deviceRequest.getOrganisationIdentification())
        .deviceIdentification(deviceRequest.getDeviceIdentification())
        .correlationUid(deviceRequest.getCorrelationUid())
        .domain(deviceRequest.getDomain())
        .domainVersion(deviceRequest.getDomainVersion())
        .messageType(deviceRequest.getMessageType())
        .messagePriority(deviceRequest.getMessagePriority())
        .ipAddress(deviceRequest.getIpAddress())
        .retryCount(deviceRequest.getRetryCount())
        .isScheduled(deviceRequest.isScheduled());
  }
}
