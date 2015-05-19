package com.alliander.osgp.adapter.ws.smartmetering.infra.jms;

import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;

import com.alliander.osgp.shared.infra.jms.BaseResponseMessageFinder;

/**
 * Class for retrieving response messages from the smart metering responses
 * queue by correlation UID.
 *
 * @author CGI
 *
 */
public class SmartMeteringResponseMessageFinder extends BaseResponseMessageFinder {

    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringResponseMessageFinder.class);

    /**
     * Autowired JMS template for OSGP domain smart metering responses queue.
     */
    @Autowired
    @Qualifier("wsSmartMeteringIncomingResponsesJmsTemplate")
    private JmsTemplate smartMeteringResponsesJmsTemplate;

    @Override
    protected ObjectMessage receiveObjectMessage(final String correlationUid) {
        LOGGER.info("Trying to find message with correlationUID: {}", correlationUid);

        return (ObjectMessage) this.smartMeteringResponsesJmsTemplate.receiveSelected(this
                .getJmsCorrelationId(correlationUid));
    }
}
