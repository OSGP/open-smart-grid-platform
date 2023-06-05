// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.infra.jms.domain.outbound;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.opensmartgridplatform.core.domain.model.domain.DomainResponseService;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.ProtocolRequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class DomainResponseMessageSender implements DomainResponseService {

  @Autowired private DomainResponseMessageJmsTemplateFactory factory;

  @Override
  public void send(final ProtocolResponseMessage protocolResponseMessage) {

    final String key =
        DomainInfo.getKey(
            protocolResponseMessage.getDomain(), protocolResponseMessage.getDomainVersion());
    final JmsTemplate jmsTemplate = this.factory.getJmsTemplate(key);

    final ResponseMessage message = createResponseMessage(protocolResponseMessage);

    send(message, protocolResponseMessage.getMessageType(), jmsTemplate);
  }

  @Override
  public void send(final ProtocolRequestMessage protocolRequestMessage, final Exception e) {

    final String key =
        DomainInfo.getKey(
            protocolRequestMessage.getDomain(), protocolRequestMessage.getDomainVersion());
    final JmsTemplate jmsTemplate = this.factory.getJmsTemplate(key);

    final ResponseMessage message = createResponseMessage(protocolRequestMessage, e);

    send(message, protocolRequestMessage.getMessageType(), jmsTemplate);
  }

  private static void send(
      final ResponseMessage message, final String messageType, final JmsTemplate jmsTemplate) {

    jmsTemplate.send(
        new MessageCreator() {

          @Override
          public Message createMessage(final Session session) throws JMSException {
            final ObjectMessage objectMessage = session.createObjectMessage(message);
            message.messageMetadata().applyTo(objectMessage);
            objectMessage.setJMSType(messageType);
            objectMessage.setStringProperty(Constants.RESULT, message.getResult().toString());
            if (message.getOsgpException() != null) {
              objectMessage.setStringProperty(
                  Constants.DESCRIPTION, message.getOsgpException().getMessage());
            }
            return objectMessage;
          }
        });
  }

  private static ResponseMessage createResponseMessage(
      final ProtocolResponseMessage protocolResponseMessage) {

    return ResponseMessage.newResponseMessageBuilder()
        .withMessageMetadata(protocolResponseMessage.messageMetadata())
        .withResult(protocolResponseMessage.getResult())
        .withOsgpException(protocolResponseMessage.getOsgpException())
        .withDataObject(protocolResponseMessage.getDataObject())
        .build();
  }

  private static ResponseMessage createResponseMessage(
      final ProtocolRequestMessage protocolRequestMessage, final Exception e) {
    final OsgpException ex = ensureOsgpException(e);

    return ResponseMessage.newResponseMessageBuilder()
        .withMessageMetadata(protocolRequestMessage.messageMetadata())
        .withResult(ResponseMessageResultType.NOT_OK)
        .withOsgpException(ex)
        .build();
  }

  private static OsgpException ensureOsgpException(final Exception e) {

    if (e instanceof OsgpException) {
      return (OsgpException) e;
    }

    return new TechnicalException(ComponentType.OSGP_CORE, "An unknown error occurred", e);
  }
}
