package com.alliander.osgp.shared.infra.jms;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

public interface MessageProcessor {

    void processMessage(ObjectMessage message) throws JMSException;

}
