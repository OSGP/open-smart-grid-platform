// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.infra.jms.protocol.inbound.messageprocessors;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.core.application.services.EventNotificationMessageService;
import org.opensmartgridplatform.core.infra.jms.protocol.inbound.AbstractProtocolRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushNotificationSmsDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("dlmsPushNotificationSmsMessageProcessor")
@Transactional(value = "transactionManager")
public class PushNotificationSmsMessageProcessor extends AbstractProtocolRequestMessageProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(PushNotificationSmsMessageProcessor.class);

  @Autowired private EventNotificationMessageService eventNotificationMessageService;

  @Autowired private DeviceRepository deviceRepository;

  protected PushNotificationSmsMessageProcessor() {
    super(MessageType.PUSH_NOTIFICATION_SMS);
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

      final PushNotificationSmsDto pushNotificationSms = (PushNotificationSmsDto) dataObject;

      this.storeSmsAsEvent(pushNotificationSms);

      if (pushNotificationSms.getIpAddress() != null
          && !"".equals(pushNotificationSms.getIpAddress())) {

        LOGGER.info(
            "Updating device {} IP address from {} to {}",
            metadata.getDeviceIdentification(),
            requestMessage.getIpAddress(),
            pushNotificationSms.getIpAddress());

        // Convert the IP address from String to InetAddress.
        final InetAddress address = InetAddress.getByName(pushNotificationSms.getIpAddress());

        device.updateRegistrationData(address, device.getDeviceType());
        device.updateConnectionDetailsToSuccess();
        this.deviceRepository.save(device);

      } else {
        LOGGER.warn(
            "Sms notification request for device = {} has no new IP address. Discard request.",
            metadata.getDeviceIdentification());
      }

    } catch (final UnknownHostException | FunctionalException e) {
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

  private void storeSmsAsEvent(final PushNotificationSmsDto pushNotificationSms) {
    try {
      /*
       * Push notifications for SMS don't contain date/time info, use new
       * Date() as time with the notification.
       */
      this.eventNotificationMessageService.handleEvent(
          pushNotificationSms.getDeviceIdentification(),
          Instant.now(),
          org.opensmartgridplatform.domain.core.valueobjects.EventType.SMS_NOTIFICATION,
          pushNotificationSms.getIpAddress(),
          0);
    } catch (final UnknownEntityException uee) {
      LOGGER.warn(
          "Unable to store event for Push Notification Sms from unknown device: {}",
          pushNotificationSms,
          uee);
    } catch (final Exception e) {
      LOGGER.error("Error storing event for Push Notification Sms: {}", pushNotificationSms, e);
    }
  }
}
