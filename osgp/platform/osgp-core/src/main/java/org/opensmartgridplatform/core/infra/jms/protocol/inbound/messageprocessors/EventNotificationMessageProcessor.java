//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.core.infra.jms.protocol.inbound.messageprocessors;

import java.util.List;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.core.application.services.EventNotificationMessageService;
import org.opensmartgridplatform.core.infra.jms.protocol.inbound.AbstractProtocolRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("oslpEventNotificationMessageProcessor")
public class EventNotificationMessageProcessor extends AbstractProtocolRequestMessageProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(EventNotificationMessageProcessor.class);

  @Autowired private EventNotificationMessageService eventNotificationMessageService;

  protected EventNotificationMessageProcessor() {
    super(MessageType.EVENT_NOTIFICATION);
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

      if (dataObject instanceof EventNotificationDto) {

        final EventNotificationDto eventNotification = (EventNotificationDto) dataObject;
        this.eventNotificationMessageService.handleEvent(
            metadata.getDeviceIdentification(), eventNotification);

      } else if (dataObject instanceof List) {
        final List<EventNotificationDto> eventNotificationDtoList =
            (List<EventNotificationDto>) dataObject;
        this.eventNotificationMessageService.handleEvents(
            metadata.getDeviceIdentification(), eventNotificationDtoList);
      }

    } catch (final UnknownEntityException e) {
      final String errorMessage =
          String.format("%s occurred, reason: %s", e.getClass().getName(), e.getMessage());
      LOGGER.error(errorMessage, e);

      throw new JMSException(errorMessage);
    }
  }
}
