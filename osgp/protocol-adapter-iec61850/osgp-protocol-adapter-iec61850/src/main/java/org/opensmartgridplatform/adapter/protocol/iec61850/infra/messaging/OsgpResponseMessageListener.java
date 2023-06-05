// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.infra.jms.UnknownMessageTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(value = "protocolIec61850InboundOsgpCoreResponsesMessageListener")
public class OsgpResponseMessageListener implements MessageListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(OsgpResponseMessageListener.class);

  @Override
  public void onMessage(final Message message) {
    try {
      LOGGER.info("Received message of type: {}", message.getJMSType());

      final ObjectMessage objectMessage = (ObjectMessage) message;
      final String messageType = objectMessage.getJMSType();
      final String deviceIdentification =
          objectMessage.getStringProperty(Constants.DEVICE_IDENTIFICATION);
      final ResponseMessage responseMessage = (ResponseMessage) objectMessage.getObject();
      final String result = responseMessage == null ? null : responseMessage.getResult().toString();
      final OsgpException osgpException =
          responseMessage == null ? null : responseMessage.getOsgpException();

      if (MessageType.valueOf(messageType) == (MessageType.REGISTER_DEVICE)) {
        handleDeviceRegistration(result, deviceIdentification, messageType, osgpException);
      } else {
        throw new UnknownMessageTypeException("Unknown JMSType: " + messageType);
      }

    } catch (final JMSException ex) {
      LOGGER.error("Exception: {} ", ex.getMessage(), ex);
    } catch (final ProtocolAdapterException e) {
      LOGGER.error("ProtocolAdapterException", e);
    } catch (final UnknownMessageTypeException e) {
      LOGGER.error("UnknownMessageTypeException", e);
    }
  }

  private static void handleDeviceRegistration(
      final String result,
      final String deviceIdentification,
      final String messageType,
      final OsgpException osgpException)
      throws ProtocolAdapterException {
    if (ResponseMessageResultType.valueOf(result) == (ResponseMessageResultType.NOT_OK)) {
      throw new ProtocolAdapterException(
          String.format(
              "Response for device: %s for MessageType: %s is: %s, error: %s",
              deviceIdentification, messageType, result, osgpException));
    } else {
      LOGGER.info("Device registration successful for device: {}", deviceIdentification);
    }
  }
}
