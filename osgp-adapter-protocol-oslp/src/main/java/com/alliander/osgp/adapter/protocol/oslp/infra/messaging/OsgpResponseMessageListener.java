package com.alliander.osgp.adapter.protocol.oslp.infra.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.protocol.oslp.exceptions.ProtocolAdapterException;
import com.alliander.osgp.dto.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;
import com.alliander.osgp.shared.infra.jms.UnknownMessageTypeException;

public class OsgpResponseMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OsgpResponseMessageListener.class);

    @Override
    public void onMessage(final Message message) {
        try {
            LOGGER.info("Received message of type: {}", message.getJMSType());

            final ObjectMessage objectMessage = (ObjectMessage) message;
            final String messageType = objectMessage.getJMSType();
            final String deviceIdentifcation = objectMessage.getStringProperty(Constants.DEVICE_IDENTIFICATION);
            final ResponseMessage responseMessage = (ResponseMessage) objectMessage.getObject();
            final String result = responseMessage == null ? null : responseMessage.getResult().toString();
            final String description = responseMessage == null ? null : responseMessage.getDescription();

            switch (DeviceFunction.valueOf(messageType)) {
            case REGISTER_DEVICE:
                if (ResponseMessageResultType.valueOf(result).equals(ResponseMessageResultType.NOT_OK)) {
                    throw new ProtocolAdapterException(String.format(
                            "Response for device: %s for MessageType: %s is: %s, error: %s", deviceIdentifcation,
                            messageType, result, description));
                }
                break;

            default:
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
}
