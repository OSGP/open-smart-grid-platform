// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.infra.jms.protocol.inbound.messageprocessors;

import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.opensmartgridplatform.core.application.services.EventNotificationMessageService;
import org.opensmartgridplatform.core.domain.model.domain.DomainRequestService;
import org.opensmartgridplatform.core.infra.jms.protocol.inbound.AbstractProtocolRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.DeviceAuthorizationRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.DomainInfoRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushNotificationAlarmDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("dlmsPushNotificationAlarmMessageProcessor")
@Transactional(value = "transactionManager")
public class PushNotificationAlarmMessageProcessor extends AbstractProtocolRequestMessageProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(PushNotificationAlarmMessageProcessor.class);

  @Autowired private EventNotificationMessageService eventNotificationMessageService;

  @Autowired private DomainRequestService domainRequestService;

  @Autowired private DomainInfoRepository domainInfoRepository;

  @Autowired private DeviceAuthorizationRepository deviceAuthorizationRepository;

  @Autowired private DeviceRepository deviceRepository;

  protected PushNotificationAlarmMessageProcessor() {
    super(MessageType.PUSH_NOTIFICATION_ALARM);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {

    final MessageMetadata metadata = MessageMetadata.fromMessage(message);

    LOGGER.info(
        "Received message of messageType: {} organisationIdentification: {} deviceIdentification: {}",
        this.messageType,
        metadata.getOrganisationIdentification(),
        metadata.getDeviceIdentification());

    final RequestMessage requestMessage = (RequestMessage) message.getObject();
    final Object dataObject = requestMessage.getRequest();

    try {

      final Device device = this.getDevice(metadata.getDeviceIdentification());

      final PushNotificationAlarmDto pushNotificationAlarm = (PushNotificationAlarmDto) dataObject;

      this.storeAlarmAsEvent(pushNotificationAlarm);

      final String ownerIdentification = this.getOrganisationIdentificationOfOwner(device);

      LOGGER.info(
          "Matching owner {} with device {} handling {} from {}",
          ownerIdentification,
          metadata.getDeviceIdentification(),
          this.messageType,
          requestMessage.getIpAddress());
      final RequestMessage requestWithUpdatedOrganization =
          new RequestMessage(
              requestMessage.getCorrelationUid(),
              ownerIdentification,
              requestMessage.getDeviceIdentification(),
              requestMessage.getIpAddress(),
              requestMessage.getBaseTransceiverStationId(),
              requestMessage.getCellId(),
              pushNotificationAlarm);

      final Optional<DomainInfo> smartMeteringDomain = this.getDomainInfo();

      if (smartMeteringDomain.isPresent()) {
        this.domainRequestService.send(
            requestWithUpdatedOrganization,
            DeviceFunction.PUSH_NOTIFICATION_ALARM.name(),
            smartMeteringDomain.get());

        device.updateConnectionDetailsToSuccess();
        this.deviceRepository.save(device);
      } else {
        LOGGER.error(
            "No DomainInfo found for SMART_METERING 1.0, unable to send message of message type: {} to "
                + "domain adapter. RequestMessage for {} dropped.",
            this.messageType,
            pushNotificationAlarm);
      }

    } catch (final OsgpException e) {
      final String errorMessage =
          String.format("%s occurred, reason: %s", e.getClass().getName(), e.getMessage());
      LOGGER.error(errorMessage, e);

      throw new JMSException(errorMessage);
    }
  }

  private Device getDevice(final String deviceIdentification) throws FunctionalException {
    final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);

    if (device == null) {
      LOGGER.error(
          "No known device for deviceIdentification {} with alarm notification",
          deviceIdentification);
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_DEVICE,
          ComponentType.OSGP_CORE,
          new UnknownEntityException(Device.class, deviceIdentification));
    }
    return device;
  }

  private Optional<DomainInfo> getDomainInfo() {
    /*
     * This message processor handles messages that came in on the
     * osgp-core.1_0.protocol-dlms.1_0.requests queue. Therefore lookup
     * the DomainInfo for DLMS (domain: SMART_METERING) version 1.0.
     *
     * At some point in time there may be a cleaner solution, where the
     * DomainInfo can be derived from information in the message or JMS
     * metadata, but for now this will have to do.
     */
    final List<DomainInfo> domainInfos = this.domainInfoRepository.findAll();

    return domainInfos.stream()
        .filter(d -> "SMART_METERING".equals(d.getDomain()) && "1.0".equals(d.getDomainVersion()))
        .findFirst();
  }

  private void storeAlarmAsEvent(final PushNotificationAlarmDto pushNotificationAlarm) {
    try {
      /*
       * Push notifications for alarms don't contain date/time info, use
       * new Date() as time with the notification.
       */
      this.eventNotificationMessageService.handleEvent(
          pushNotificationAlarm.getDeviceIdentification(),
          Instant.now(),
          org.opensmartgridplatform.domain.core.valueobjects.EventType.ALARM_NOTIFICATION,
          pushNotificationAlarm.getAlarms().toString(),
          0);
    } catch (final UnknownEntityException uee) {
      LOGGER.warn(
          "Unable to store event for Push Notification Alarm from unknown device: {}",
          pushNotificationAlarm,
          uee);
    } catch (final Exception e) {
      LOGGER.error("Error storing event for Push Notification Alarm: {}", pushNotificationAlarm, e);
    }
  }

  private String getOrganisationIdentificationOfOwner(final Device device) throws OsgpException {

    final List<DeviceAuthorization> deviceAuthorizations =
        this.deviceAuthorizationRepository.findByDeviceAndFunctionGroup(
            device, DeviceFunctionGroup.OWNER);

    if (deviceAuthorizations == null || deviceAuthorizations.isEmpty()) {
      LOGGER.error(
          "No owner authorization for deviceIdentification {} with alarm notification",
          device.getDeviceIdentification());
      throw new FunctionalException(
          FunctionalExceptionType.UNAUTHORIZED,
          ComponentType.OSGP_CORE,
          new UnknownEntityException(DeviceAuthorization.class, device.getDeviceIdentification()));
    }

    return deviceAuthorizations.get(0).getOrganisation().getOrganisationIdentification();
  }
}
