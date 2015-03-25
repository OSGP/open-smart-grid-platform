package com.alliander.osgp.core.infra.jms.protocol.logging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.OslpLogItem;
import com.alliander.osgp.domain.core.exceptions.OsgpCoreException;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OslpLogItemRepository;
import com.alliander.osgp.shared.infra.jms.Constants;

//This class should fetch incoming messages from a logging requests queue.
public class ProtocolLogItemRequestMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolLogItemRequestMessageListener.class);

    // TODO: This repository is in domain-core! Should this be changed perhaps?

    @Autowired
    private OslpLogItemRepository oslpLogItemRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    public void onMessage(final Message message) {

        try {
            final ObjectMessage objectMessage = (ObjectMessage) message;
            final String messageType = objectMessage.getJMSType();

            LOGGER.info("Received protocol log item request message off type [{}]", messageType);

            switch (messageType) {

            case Constants.OSLP_LOG_ITEM_REQUEST:
                this.handleOslpLogMessage(objectMessage);
                break;

            default:
                throw new OsgpCoreException("Unknown JMSType: " + messageType);
            }

        } catch (final JMSException e) {
            LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
        } catch (final OsgpCoreException e) {
            LOGGER.error("OsgpCoreException", e);
        }
    }

    private void handleOslpLogMessage(final ObjectMessage objectMessage) throws JMSException {

        final String deviceIdentification = objectMessage.getStringProperty(Constants.DEVICE_IDENTIFICATION);
        String organisationIdentification = objectMessage.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
        if (StringUtils.isEmpty(organisationIdentification)) {
            final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
            organisationIdentification = device.getOwner();
        }

        final OslpLogItem oslpLogItem = new OslpLogItem(organisationIdentification,
                objectMessage.getStringProperty(Constants.DEVICE_UID), deviceIdentification,
                Boolean.parseBoolean(objectMessage.getStringProperty(Constants.IS_INCOMING)),
                Boolean.parseBoolean(objectMessage.getStringProperty(Constants.IS_VALID)),
                objectMessage.getStringProperty(Constants.ENCODED_MESSAGE),
                objectMessage.getStringProperty(Constants.DECODED_MESSAGE),
                objectMessage.getIntProperty(Constants.PAYLOAD_MESSAGE_SERIALIZED_SIZE));
        this.oslpLogItemRepository.save(oslpLogItem);
    }
}
