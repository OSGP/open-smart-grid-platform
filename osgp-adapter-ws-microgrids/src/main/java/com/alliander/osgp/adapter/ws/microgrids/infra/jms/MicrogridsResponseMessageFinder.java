package com.alliander.osgp.adapter.ws.microgrids.infra.jms;

import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;

import com.alliander.osgp.shared.infra.jms.BaseResponseMessageFinder;

public class MicrogridsResponseMessageFinder extends BaseResponseMessageFinder {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MicrogridsResponseMessageFinder.class);

    /**
     * Autowired JMS template for OSGP domain public lighting responses queue.
     */
    @Autowired
    @Qualifier("wsMicrogridsIncomingResponsesJmsTemplate")
    private JmsTemplate microgridsResponsesJmsTemplate;

    @Override
    protected ObjectMessage receiveObjectMessage(final String correlationUid) {
        LOGGER.info("Trying to find message with correlationUID: {}", correlationUid);

        return (ObjectMessage) this.microgridsResponsesJmsTemplate
                .receiveSelected(this.getJmsCorrelationId(correlationUid));
    }

}