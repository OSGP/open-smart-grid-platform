package com.alliander.osgp.adapter.domain.publiclighting.infra.jms.ws.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.publiclighting.application.services.AdHocManagementService;
import com.alliander.osgp.adapter.domain.publiclighting.infra.jms.ws.WebServiceRequestMessageProcessor;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.TransitionMessageDataContainer;
import com.alliander.osgp.shared.infra.jms.Constants;

/**
 * Class for processing public lighting set transition request messages
 * 
 * @author CGI
 * 
 */
@Component("domainPublicLightingSetTransitionRequestMessageProcessor")
public class PublicLightingSetTransitionRequestMessageProcessor extends WebServiceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PublicLightingSetTransitionRequestMessageProcessor.class);

    @Autowired
    @Qualifier("domainPublicLightingAdHocManagementService")
    private AdHocManagementService adHocManagementService;

    public PublicLightingSetTransitionRequestMessageProcessor() {
        super(DeviceFunction.SET_TRANSITION);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing public lighting set transition request message");

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

            final TransitionMessageDataContainer transitionMessageDataContainer = (TransitionMessageDataContainer) dataObject;

            this.adHocManagementService.setTransition(organisationIdentification, deviceIdentification, correlationUid,
                    transitionMessageDataContainer.getTransitionType(), transitionMessageDataContainer.getDateTime(),
                    messageType);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, messageType);
        }
    }
}
