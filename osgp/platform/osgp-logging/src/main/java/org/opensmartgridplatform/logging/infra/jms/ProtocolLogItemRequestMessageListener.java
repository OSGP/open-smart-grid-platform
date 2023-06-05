// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.logging.infra.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.opensmartgridplatform.logging.domain.repositories.DeviceLogItemPagingRepository;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// This class should fetch incoming messages from a logging requests queue.
@Component(value = "OsgpLoggingInboundProtocolLogItemRequestsMessageListener")
public class ProtocolLogItemRequestMessageListener implements MessageListener {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ProtocolLogItemRequestMessageListener.class);

  @Autowired private DeviceLogItemPagingRepository deviceLogRepository;

  @Override
  public void onMessage(final Message message) {

    try {
      if (message instanceof TextMessage) {
        LOGGER.warn(
            "A TextMessage is received. TextMessages belong with configuration setting : auditlogging.message.create.json=true");
        return;
      }
      final ObjectMessage objectMessage = (ObjectMessage) message;
      final String messageType = objectMessage.getJMSType();

      LOGGER.info("Received protocol log item request message of type [{}]", messageType);

      this.handleDeviceLogMessage(objectMessage);
    } catch (final JMSException e) {
      LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
    }
  }

  private void handleDeviceLogMessage(final ObjectMessage objectMessage) throws JMSException {

    final String deviceIdentification =
        objectMessage.getStringProperty(Constants.DEVICE_IDENTIFICATION);
    final String organisationIdentification =
        objectMessage.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);

    final DeviceLogItem deviceLogItem =
        new DeviceLogItem.Builder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceUid(objectMessage.getStringProperty(Constants.DEVICE_UID))
            .withDeviceIdentification(deviceIdentification)
            .withIncoming(
                Boolean.parseBoolean(objectMessage.getStringProperty(Constants.IS_INCOMING)))
            .withValid(Boolean.parseBoolean(objectMessage.getStringProperty(Constants.IS_VALID)))
            .withEncodedMessage(objectMessage.getStringProperty(Constants.ENCODED_MESSAGE))
            .withDecodedMessage(objectMessage.getStringProperty(Constants.DECODED_MESSAGE))
            .withPayloadMessageSerializedSize(
                objectMessage.getIntProperty(Constants.PAYLOAD_MESSAGE_SERIALIZED_SIZE))
            .build();
    this.deviceLogRepository.save(deviceLogItem);
  }
}
