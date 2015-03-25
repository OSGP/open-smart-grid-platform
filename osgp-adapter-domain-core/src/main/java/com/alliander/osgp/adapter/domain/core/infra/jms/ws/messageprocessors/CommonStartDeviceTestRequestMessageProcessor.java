package com.alliander.osgp.adapter.domain.core.infra.jms.ws.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.core.application.services.DeviceInstallationService;
import com.alliander.osgp.adapter.domain.core.infra.jms.ws.WebServiceRequestMessageProcessor;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.infra.jms.Constants;

/**
 * Class for processing common start device test request messages
 * 
 * @author CGI
 * 
 */
@Component("domainCoreCommonStartDeviceTestRequestMessageProcessor")
public class CommonStartDeviceTestRequestMessageProcessor extends WebServiceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonStartDeviceTestRequestMessageProcessor.class);

    @Autowired
    @Qualifier("domainCoreDeviceInstallationService")
    private DeviceInstallationService deviceInstallationService;

    public CommonStartDeviceTestRequestMessageProcessor() {
        super(DeviceFunction.START_SELF_TEST);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing common start device test request message");

        String correlationUid = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;

        try {
            correlationUid = message.getJMSCorrelationID();
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
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

            this.deviceInstallationService.startSelfTest(deviceIdentification, organisationIdentification,
                    correlationUid, messageType);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, messageType);
        }
    }
}
