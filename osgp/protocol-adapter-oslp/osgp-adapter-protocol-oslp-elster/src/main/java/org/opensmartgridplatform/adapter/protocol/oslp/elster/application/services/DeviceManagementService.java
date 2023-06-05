// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.services;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.services.oslp.OslpDeviceSettingsService;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.services.oslp.PendingSetScheduleRequestService;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SetScheduleDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.entities.OslpDevice;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.entities.PendingSetScheduleRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OsgpRequestMessageSender;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking.DeviceService;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationDto;
import org.opensmartgridplatform.dto.valueobjects.EventTypeDto;
import org.opensmartgridplatform.dto.valueobjects.RelayTypeDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleMessageDataContainerDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleMessageTypeDto;
import org.opensmartgridplatform.oslp.Oslp;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service(value = "oslpDeviceManagementService")
@Transactional(value = "transactionManager")
public class DeviceManagementService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementService.class);

  @Autowired private OslpDeviceSettingsService oslpDeviceSettingsService;

  @Autowired private OsgpRequestMessageSender osgpRequestMessageSender;

  @Autowired private PendingSetScheduleRequestService pendingSetScheduleRequestService;

  @Autowired private DeviceService deviceService;

  /** Constructor */
  public DeviceManagementService() {
    // Parameterless constructor required for transactions...
  }

  // === ADD EVENT NOTIFICATION ===

  /**
   * Create a new event notification DTO with the given arguments.
   *
   * @param deviceIdentification The identification of the device.
   * @param deviceUid The UID of the device.
   * @param eventType The event type. May not be empty or null.
   * @param description The description which came along with the event from the device. May be an
   *     empty string, but not null.
   * @param index The index of the relay. May not be null.
   * @param timestamp The date and time of the event. May be an empty string or null.
   */
  private EventNotificationDto createEventNotificationDto(
      final String deviceIdentification,
      final String deviceUid,
      final String eventType,
      final String description,
      final Integer index,
      final String timestamp) {
    Assert.notNull(eventType, "event type must not be null");
    Assert.notNull(description, "description must not be null");
    Assert.notNull(index, "index must not be null");

    LOGGER.info(
        "addEventNotification called for device: {} with eventType: {}, description: {} and timestamp: {}",
        deviceIdentification,
        eventType,
        description,
        timestamp);

    // Convert timestamp to DateTime.
    final DateTime dateTime;
    if (StringUtils.isEmpty(timestamp)) {
      dateTime = DateTime.now();
      LOGGER.info("timestamp is empty, using DateTime.now(): {}", dateTime);
    } else {
      final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss Z");
      dateTime = dateTimeFormatter.withOffsetParsed().parseDateTime(timestamp.concat(" +0000"));
      LOGGER.info("parsed timestamp from string: {} to DateTime: {}", timestamp, dateTime);
    }

    return new EventNotificationDto(
        deviceUid, dateTime, EventTypeDto.valueOf(eventType), description, index);
  }

  /**
   * Send a list of event notifications to OSGP Core.
   *
   * @param deviceUid The identification of the device.
   * @param eventNotifications The event notifications.
   */
  public void addEventNotifications(
      final String deviceUid, final List<Oslp.EventNotification> eventNotifications) {
    LOGGER.info("addEventNotifications called for device {}", deviceUid);
    final OslpDevice oslpDevice = this.oslpDeviceSettingsService.getDeviceByUid(deviceUid);
    final String deviceIdentification = oslpDevice.getDeviceIdentification();

    final List<EventNotificationDto> eventNotificationDtos = new ArrayList<>();
    for (final Oslp.EventNotification eventNotification : eventNotifications) {
      final String eventType = eventNotification.getEvent().name();
      final String description = eventNotification.getDescription();
      final int index =
          eventNotification.getIndex().isEmpty() ? 0 : (int) eventNotification.getIndex().byteAt(0);
      String timestamp = eventNotification.getTimestamp();
      LOGGER.debug("-->> timestamp: {}", timestamp);
      // Hack for faulty firmware version. RTC_NOT_SET event can contain
      // illegal timestamp value of 20000000xxxxxx.
      if (!StringUtils.isEmpty(timestamp) && timestamp.startsWith("20000000")) {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");
        timestamp = DateTime.now().withZone(DateTimeZone.UTC).toString(dateTimeFormatter);
        LOGGER.info("Using DateTime.now() instead of '20000000xxxxxx', value is: {}", timestamp);
      }
      final EventNotificationDto dto =
          this.createEventNotificationDto(
              deviceIdentification, deviceUid, eventType, description, index, timestamp);
      eventNotificationDtos.add(dto);
    }

    final RequestMessage requestMessage =
        new RequestMessage(
            "no-correlationUid",
            "no-organisation",
            deviceIdentification,
            new ArrayList<>(eventNotificationDtos));

    this.osgpRequestMessageSender.send(requestMessage, MessageType.EVENT_NOTIFICATION.name());
  }

  // === UPDATE KEY ===

  public void updateKey(
      final MessageMetadata messageMetadata,
      final DeviceResponseMessageSender responseMessageSender,
      final String publicKey) {

    final String deviceIdentification = messageMetadata.getDeviceIdentification();
    final String organisationIdentification = messageMetadata.getOrganisationIdentification();
    LOGGER.info(
        "updateKey called for device: {} for organisation: {} with new publicKey: {}",
        deviceIdentification,
        organisationIdentification,
        publicKey);

    try {
      OslpDevice oslpDevice =
          this.oslpDeviceSettingsService.getDeviceByDeviceIdentification(deviceIdentification);
      if (oslpDevice == null) {
        // Device not found, create new device
        LOGGER.debug("Device [{}] does not exist, creating new device", deviceIdentification);
        oslpDevice = new OslpDevice(deviceIdentification);
        oslpDevice = this.oslpDeviceSettingsService.addDevice(oslpDevice);
      }

      oslpDevice.updatePublicKey(publicKey);
      this.oslpDeviceSettingsService.updateDevice(oslpDevice);

      this.sendResponseMessage(
          messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender);

    } catch (final Exception e) {
      LOGGER.error("Unexpected exception during updateKey", e);
      final TechnicalException ex =
          new TechnicalException(ComponentType.UNKNOWN, "Exception occurred while updating key", e);

      this.sendResponseMessage(
          messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
    }
  }

  // === REVOKE KEY ===

  public void revokeKey(
      final MessageMetadata messageMetadata,
      final DeviceResponseMessageSender responseMessageSender) {

    final String deviceIdentification = messageMetadata.getDeviceIdentification();
    final String organisationIdentification = messageMetadata.getOrganisationIdentification();
    LOGGER.info(
        "revokeKey called for device: {} for organisation: {}",
        deviceIdentification,
        organisationIdentification);

    try {
      final OslpDevice oslpDevice =
          this.oslpDeviceSettingsService.getDeviceByDeviceIdentification(deviceIdentification);
      if (oslpDevice == null) {
        throw new ProtocolAdapterException(
            String.format("Device not found: %s", deviceIdentification));
      }

      oslpDevice.revokePublicKey();
      this.oslpDeviceSettingsService.updateDevice(oslpDevice);

      this.sendResponseMessage(
          messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender);

    } catch (final Exception e) {
      LOGGER.error("Unexpected exception during revokeKey", e);
      final TechnicalException ex =
          new TechnicalException(ComponentType.UNKNOWN, "Exception occurred while revoking key", e);
      this.sendResponseMessage(
          messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
    }
  }

  private void sendResponseMessage(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType result,
      final OsgpException osgpException,
      final DeviceResponseMessageSender responseMessageSender) {

    final ProtocolResponseMessage responseMessage =
        ProtocolResponseMessage.newBuilder()
            .messageMetadata(messageMetadata)
            .result(result)
            .osgpException(osgpException)
            .build();

    responseMessageSender.send(responseMessage);
  }

  public void handleSetSchedule(final String deviceUid) throws TechnicalException {
    final List<PendingSetScheduleRequest> pendingSetScheduleRequestList =
        this.pendingSetScheduleRequestService.getAllByDeviceUidNotExpired(deviceUid);
    if (pendingSetScheduleRequestList.isEmpty()) {
      // Resume the default flow
      // This confirm register device request was not triggered by a set
      // schedule with astronomical offsets
      return;
    }

    if (pendingSetScheduleRequestList.size() > 1) {
      throw new TechnicalException(
          String.format(
              "Currently there are %d pending set schedule requests for device %s. Only one is allowed.",
              pendingSetScheduleRequestList.size(), deviceUid));
    }

    final PendingSetScheduleRequest pendingSetScheduleRequest =
        pendingSetScheduleRequestList.get(0);

    final ScheduleMessageDataContainerDto dto =
        pendingSetScheduleRequest.getScheduleMessageDataContainerDto();
    ScheduleMessageDataContainerDto.Builder builder =
        new ScheduleMessageDataContainerDto.Builder(dto.getSchedule());
    builder = builder.withScheduleMessageType(ScheduleMessageTypeDto.SET_SCHEDULE);
    final ScheduleMessageDataContainerDto scheduleMessageDataContainer = builder.build();

    final DeviceRequest deviceRequest = pendingSetScheduleRequest.getDeviceRequest();
    final MessageMetadata messageMetadata = this.getMessageMetadataFromDeviceRequest(deviceRequest);

    final SetScheduleDeviceRequest newDeviceRequest =
        new SetScheduleDeviceRequest(
            DeviceRequest.newBuilder().messageMetaData(messageMetadata),
            scheduleMessageDataContainer,
            RelayTypeDto.LIGHT);

    this.deviceService.setSchedule(newDeviceRequest);
    this.pendingSetScheduleRequestService.remove(pendingSetScheduleRequest);
  }

  private MessageMetadata getMessageMetadataFromDeviceRequest(final DeviceRequest deviceRequest) {
    return MessageMetadata.newBuilder()
        .withCorrelationUid(deviceRequest.getCorrelationUid())
        .withOrganisationIdentification(deviceRequest.getOrganisationIdentification())
        .withDeviceIdentification(deviceRequest.getDeviceIdentification())
        .withMessageType(deviceRequest.getMessageType())
        .withDomain(deviceRequest.getDomain())
        .withDomainVersion(deviceRequest.getDomainVersion())
        .withIpAddress(deviceRequest.getIpAddress())
        .withMessagePriority(deviceRequest.getMessagePriority())
        .withScheduled(deviceRequest.isScheduled())
        .withRetryCount(deviceRequest.getRetryCount())
        .build();
  }
}
