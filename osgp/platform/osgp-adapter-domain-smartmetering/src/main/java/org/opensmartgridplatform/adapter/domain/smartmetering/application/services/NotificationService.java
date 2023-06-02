//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import ma.glasnost.orika.MapperFactory;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushNotificationAlarm;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SystemEvent;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushNotificationAlarmDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SystemEventDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "domainSmartMeteringNotificationService")
public class NotificationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

  @Autowired private MapperFactory mapperFactory;

  @Autowired private WebServiceResponseMessageSender webServiceResponseMessageSender;

  public void handlePushNotificationAlarm(
      final MessageMetadata messageMetadata, final PushNotificationAlarmDto pushNotificationAlarm) {

    LOGGER.info(
        "handlePushNotificationAlarm for MessageType: {}", messageMetadata.getMessageType());

    final PushNotificationAlarm pushNotificationAlarmDomain =
        this.mapperFactory
            .getMapperFacade()
            .map(pushNotificationAlarm, PushNotificationAlarm.class);

    /*
     * Send the push notification alarm as a response message to the web service, so
     * it can be handled similar to response messages based on earlier web service
     * requests.
     */
    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withMessageMetadata(messageMetadata)
            .withResult(ResponseMessageResultType.OK)
            .withDataObject(pushNotificationAlarmDomain)
            .build();
    this.webServiceResponseMessageSender.send(responseMessage, messageMetadata.getMessageType());
  }

  public void handleSystemEvent(
      final MessageMetadata messageMetadata, final SystemEventDto systemEventDto) {

    LOGGER.info("handleSystemEvent for MessageType: {}", messageMetadata.getMessageType());

    final SystemEvent systemEvent =
        this.mapperFactory.getMapperFacade().map(systemEventDto, SystemEvent.class);

    /*
     * Send the systemEvent as a response message to the web service, so
     * it can be handled similar to response messages based on earlier web service
     * requests.
     */
    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withMessageMetadata(messageMetadata)
            .withResult(ResponseMessageResultType.OK)
            .withDataObject(systemEvent)
            .build();
    this.webServiceResponseMessageSender.send(responseMessage, messageMetadata.getMessageType());
  }
}
