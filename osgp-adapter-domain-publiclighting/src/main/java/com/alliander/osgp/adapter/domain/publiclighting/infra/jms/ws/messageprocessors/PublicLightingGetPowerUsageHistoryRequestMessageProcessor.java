package com.alliander.osgp.adapter.domain.publiclighting.infra.jms.ws.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.publiclighting.application.services.DeviceMonitoringService;
import com.alliander.osgp.adapter.domain.publiclighting.infra.jms.ws.WebServiceRequestMessageProcessor;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.PowerUsageHistoryMessageDataContainer;
import com.alliander.osgp.shared.infra.jms.Constants;

/**
 * Class for processing public lighting get power usage history request messages
 * 
 * @author CGI
 * 
 */
@Component("domainPublicLightingGetPowerUsageHistoryRequestMessageProcessor")
public class PublicLightingGetPowerUsageHistoryRequestMessageProcessor extends WebServiceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicLightingGetPowerUsageHistoryRequestMessageProcessor.class);

    @Autowired
    @Qualifier("domainPublicLightingDeviceMonitoringService")
    private DeviceMonitoringService deviceMonitoringService;

    public PublicLightingGetPowerUsageHistoryRequestMessageProcessor() {
        super(DeviceFunction.GET_POWER_USAGE_HISTORY);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing public lighting get power usage history request message");

        String correlationUid = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;
        Object dataObject = null;
        Long scheduleTime = null;

        try {
            correlationUid = message.getJMSCorrelationID();
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
            dataObject = message.getObject();
            if (message.propertyExists(Constants.SCHEDULE_TIME)) {
                scheduleTime = message.getLongProperty(Constants.SCHEDULE_TIME);
            }
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

            final PowerUsageHistoryMessageDataContainer powerUsageHistoryMessageDataContainer = (PowerUsageHistoryMessageDataContainer) dataObject;

            this.deviceMonitoringService.getPowerUsageHistory(organisationIdentification, deviceIdentification, correlationUid,
                    powerUsageHistoryMessageDataContainer.getTimePeriod(), powerUsageHistoryMessageDataContainer.getHistoryTermType(), scheduleTime,
                    messageType);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, messageType);
        }
    }
}
