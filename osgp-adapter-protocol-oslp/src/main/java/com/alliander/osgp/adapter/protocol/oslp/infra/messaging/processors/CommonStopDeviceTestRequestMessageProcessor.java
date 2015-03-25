package com.alliander.osgp.adapter.protocol.oslp.infra.messaging.processors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.DeviceResponse;
import com.alliander.osgp.adapter.protocol.oslp.device.DeviceResponseHandler;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.DeviceRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.DeviceRequestMessageType;
import com.alliander.osgp.shared.infra.jms.Constants;

/**
 * Class for processing common stop device test request message
 * 
 * @author CGI
 * 
 */
@Component("oslpCommonStopDeviceTestRequestMessageProcessor")
public class CommonStopDeviceTestRequestMessageProcessor extends DeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonStopDeviceTestRequestMessageProcessor.class);

    public CommonStopDeviceTestRequestMessageProcessor() {
        super(DeviceRequestMessageType.STOP_SELF_TEST);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing common stop device test request message");

        String correlationUid = null;
        String domain = null;
        String domainVersion = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;
        String ipAddress = null;
        int retryCount = 0;

        try {
            correlationUid = message.getJMSCorrelationID();
            domain = message.getStringProperty(Constants.DOMAIN);
            domainVersion = message.getStringProperty(Constants.DOMAIN_VERSION);
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
            ipAddress = message.getStringProperty(Constants.IP_ADDRESS);
            retryCount = message.getIntProperty(Constants.RETRY_COUNT);
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.");
            LOGGER.debug("correlationUid: {}", correlationUid);
            LOGGER.debug("domain: {}", domain);
            LOGGER.debug("domainVersion: {}", domainVersion);
            LOGGER.debug("messageType: {}", messageType);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            LOGGER.debug("ipAddress: {}", ipAddress);
            return;
        }

        try {
            LOGGER.info("Calling DeviceService function: {} for domain: {} {}", messageType, domain, domainVersion);

            final DeviceResponseHandler deviceResponseHandler = new DeviceResponseHandler() {

                @Override
                public void handleResponse(final DeviceResponse deviceResponse) {
                    try {
                        CommonStopDeviceTestRequestMessageProcessor.this.handleEmptyDeviceResponse(deviceResponse,
                                CommonStopDeviceTestRequestMessageProcessor.this.responseMessageSender,
                                message.getStringProperty(Constants.DOMAIN),
                                message.getStringProperty(Constants.DOMAIN_VERSION), message.getJMSType(),
                                message.getIntProperty(Constants.RETRY_COUNT));
                    } catch (final JMSException e) {
                        LOGGER.error("JMSException", e);
                    }

                }

                @Override
                public void handleException(final Throwable t, final DeviceResponse deviceResponse) {
                    try {
                        CommonStopDeviceTestRequestMessageProcessor.this.handleUnableToConnectDeviceResponse(
                                deviceResponse,
                                t,
                                null,
                                CommonStopDeviceTestRequestMessageProcessor.this.responseMessageSender,
                                deviceResponse,
                                message.getStringProperty(Constants.DOMAIN),
                                message.getStringProperty(Constants.DOMAIN_VERSION),
                                message.getJMSType(),
                                message.propertyExists(Constants.IS_SCHEDULED) ? message
                                        .getBooleanProperty(Constants.IS_SCHEDULED) : false, message
                                        .getIntProperty(Constants.RETRY_COUNT));
                    } catch (final JMSException e) {
                        LOGGER.error("JMSException", e);
                    }

                }
            };

            final DeviceRequest deviceRequest = new DeviceRequest(organisationIdentification, deviceIdentification,
                    correlationUid);

            this.deviceService.stopSelfTest(deviceRequest, deviceResponseHandler, ipAddress);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, domain,
                    domainVersion, messageType, retryCount);
        }
    }
}
