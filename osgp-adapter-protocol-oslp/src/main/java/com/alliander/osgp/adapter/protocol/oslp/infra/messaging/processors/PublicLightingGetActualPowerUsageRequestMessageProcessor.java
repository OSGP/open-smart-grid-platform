package com.alliander.osgp.adapter.protocol.oslp.infra.messaging.processors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.DeviceResponse;
import com.alliander.osgp.adapter.protocol.oslp.device.DeviceResponseHandler;
import com.alliander.osgp.adapter.protocol.oslp.device.responses.GetActualPowerUsageDeviceResponse;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.DeviceRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.DeviceRequestMessageType;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.DeviceResponseMessageSender;
import com.alliander.osgp.dto.valueobjects.PowerUsageData;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

/**
 * Class for processing public lighting get power usage request messages
 * 
 * @author CGI
 * 
 */
@Component("oslpPublicLightingGetActualPowerUsageRequestMessageProcessor")
public class PublicLightingGetActualPowerUsageRequestMessageProcessor extends DeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PublicLightingGetActualPowerUsageRequestMessageProcessor.class);

    public PublicLightingGetActualPowerUsageRequestMessageProcessor() {
        super(DeviceRequestMessageType.GET_ACTUAL_POWER_USAGE);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing public lighting get actual power usage request message");

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
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
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
                        PublicLightingGetActualPowerUsageRequestMessageProcessor.this
                                .handleGetActualPowerUsageDeviceResponse(
                                        deviceResponse,
                                        PublicLightingGetActualPowerUsageRequestMessageProcessor.this.responseMessageSender,
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
                        PublicLightingGetActualPowerUsageRequestMessageProcessor.this
                                .handleUnableToConnectDeviceResponse(
                                        deviceResponse,
                                        t,
                                        null,
                                        PublicLightingGetActualPowerUsageRequestMessageProcessor.this.responseMessageSender,
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

            this.deviceService.getActualPowerUsage(deviceRequest, deviceResponseHandler, ipAddress);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, domain,
                    domainVersion, messageType, retryCount);
        }
    }

    protected void handleGetActualPowerUsageDeviceResponse(final DeviceResponse deviceResponse,
            final DeviceResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType, final int retryCount) {

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        OsgpException osgpException = null;
        PowerUsageData powerUsageData = null;

        try {
            final GetActualPowerUsageDeviceResponse response = (GetActualPowerUsageDeviceResponse) deviceResponse;
            this.deviceResponseService.handleDeviceMessageStatus(response.getStatus());
            powerUsageData = response.getActualPowerUsageData();
        } catch (final Exception e) {
            LOGGER.error("Device Response Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            osgpException= new TechnicalException(ComponentType.UNKNOWN, "Unexpected exception while retrieving response message", e);
        }

        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage(domain, domainVersion, messageType,
                deviceResponse.getCorrelationUid(), deviceResponse.getOrganisationIdentification(),
                deviceResponse.getDeviceIdentification(), result, osgpException, powerUsageData, retryCount);

        responseMessageSender.send(responseMessage);
    }
}
