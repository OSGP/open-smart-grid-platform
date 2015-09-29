package com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.smartmetering.application.services.ManagementService;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.WebServiceRequestMessageProcessor;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.FindEventsQueryMessageDataContainer;
import com.alliander.osgp.shared.infra.jms.Constants;

@Component("domainSmartmeteringFindEventsRequestMessageProcessor")
public class FindEventsRequestMessageProcessor extends WebServiceRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindEventsRequestMessageProcessor.class);

    @Autowired
    @Qualifier("domainSmartMeteringManagementService")
    private ManagementService managementService;

    protected FindEventsRequestMessageProcessor() {
        super(DeviceFunction.FIND_EVENTS);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.alliander.osgp.shared.infra.jms.MessageProcessor#processMessage(javax
     * .jms.ObjectMessage)
     */
    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {

        String correlationUid = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;
        Object dataObject = null;

        try {
            correlationUid = message.getJMSCorrelationID();
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
            dataObject = message.getObject();
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug("correlationUid: {}", correlationUid);
            LOGGER.debug("messageType: {}", messageType);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            return;
        }

        try {
            LOGGER.info("Calling application service function: {}", messageType);

            final FindEventsQueryMessageDataContainer data = (FindEventsQueryMessageDataContainer) dataObject;
            this.managementService.findEvents(organisationIdentification, deviceIdentification, correlationUid,
                    messageType, data);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, messageType);
        }

    }
}
