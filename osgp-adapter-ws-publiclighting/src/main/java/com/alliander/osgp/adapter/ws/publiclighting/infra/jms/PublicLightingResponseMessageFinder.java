package com.alliander.osgp.adapter.ws.publiclighting.infra.jms;

import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;

import com.alliander.osgp.shared.infra.jms.BaseResponseMessageFinder;

/**
 * Class for retrieving response messages from the public lighting responses
 * queue by correlation UID.
 * 
 * @author CGI
 * 
 */
public class PublicLightingResponseMessageFinder extends BaseResponseMessageFinder {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicLightingResponseMessageFinder.class);

    /**
     * Autowired JMS template for OSGP domain public lighting responses queue.
     */
    @Autowired
    @Qualifier("wsPublicLightingIncomingResponsesJmsTemplate")
    private JmsTemplate publicLightingResponsesJmsTemplate;

    @Override
    protected ObjectMessage receiveObjectMessage(final String correlationUid) {
        LOGGER.info("Trying to find message with correlationUID: {}", correlationUid);

        return (ObjectMessage) this.publicLightingResponsesJmsTemplate.receiveSelected(this
                .getJmsCorrelationId(correlationUid));
    }
}
