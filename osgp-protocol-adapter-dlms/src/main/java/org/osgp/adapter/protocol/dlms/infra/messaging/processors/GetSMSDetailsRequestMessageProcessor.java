package org.osgp.adapter.protocol.dlms.infra.messaging.processors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.osgp.adapter.protocol.dlms.application.services.AdhocService;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsDeviceMessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.dto.valueobjects.smartmetering.SMSDetails;

public class GetSMSDetailsRequestMessageProcessor extends DeviceRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetSMSDetailsRequestMessageProcessor.class);

    @Autowired
    private AdhocService adhocService;

    public GetSMSDetailsRequestMessageProcessor() {
        super(DeviceRequestMessageType.GET_SMS_DETAILS);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing get sms details request message");

        final DlmsDeviceMessageMetadata messageMetadata = new DlmsDeviceMessageMetadata();

        try {
            messageMetadata.handleMessage(message);

            final SMSDetails getSMSDetailsResponse = (SMSDetails) message.getObject();

            this.adhocService.getSMSDetails(messageMetadata, getSMSDetailsResponse, this.responseMessageSender);

        } catch (final JMSException e) {
            this.logJmsException(LOGGER, e, messageMetadata);
        }
    }

}
