package com.alliander.osgp.adapter.protocol.oslp.infra.messaging.processors;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceResponse;
import com.alliander.osgp.adapter.protocol.oslp.device.DeviceResponseHandler;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.GetPowerUsageHistoryDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.responses.GetPowerUsageHistoryDeviceResponse;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.DeviceRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.DeviceRequestMessageType;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.DeviceResponseMessageSender;
import com.alliander.osgp.dto.valueobjects.PowerUsageHistoryMessageDataContainer;
import com.alliander.osgp.dto.valueobjects.PowerUsageHistoryResponseMessageDataContainer;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

/**
 * Class for processing public lighting get power usage history request messages
 * 
 * @author CGI
 * 
 */
@Component("oslpPublicLightingGetPowerUsageHistoryRequestMessageProcessor")
public class PublicLightingGetPowerUsageHistoryRequestMessageProcessor extends DeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicLightingGetPowerUsageHistoryRequestMessageProcessor.class);

    public PublicLightingGetPowerUsageHistoryRequestMessageProcessor() {
        super(DeviceRequestMessageType.GET_POWER_USAGE_HISTORY);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing public lighting get power usage history request message");

        String correlationUid = null;
        String domain = null;
        String domainVersion = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;
        String ipAddress = null;
        Boolean isScheduled = null;
        int retryCount = 0;

        try {
            correlationUid = message.getJMSCorrelationID();
            domain = message.getStringProperty(Constants.DOMAIN);
            domainVersion = message.getStringProperty(Constants.DOMAIN_VERSION);
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
            ipAddress = message.getStringProperty(Constants.IP_ADDRESS);
            isScheduled = message.getBooleanProperty(Constants.IS_SCHEDULED);
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
            LOGGER.debug("scheduled: {}", isScheduled);
            return;
        }

        try {
            final PowerUsageHistoryMessageDataContainer powerUsageHistoryMessageDataContainerDto = (PowerUsageHistoryMessageDataContainer) message.getObject();

            LOGGER.info("Calling DeviceService function: {} for domain: {} {}", messageType, domain, domainVersion);

            final DeviceResponseHandler deviceResponseHandler = new DeviceResponseHandler() {

                @Override
                public void handleResponse(final DeviceResponse deviceResponse) {
                    try {
                        PublicLightingGetPowerUsageHistoryRequestMessageProcessor.this.handleGetPowerUsageHistoryDeviceResponse(deviceResponse,
                                powerUsageHistoryMessageDataContainerDto, PublicLightingGetPowerUsageHistoryRequestMessageProcessor.this.responseMessageSender,
                                message.getStringProperty(Constants.DOMAIN), message.getStringProperty(Constants.DOMAIN_VERSION), message.getJMSType(),
                                message.propertyExists(Constants.IS_SCHEDULED) ? message.getBooleanProperty(Constants.IS_SCHEDULED) : false,
                                message.getIntProperty(Constants.RETRY_COUNT));
                    } catch (final JMSException e) {
                        LOGGER.error("JMSException", e);
                    }

                }

                @Override
                public void handleException(final Throwable t, final DeviceResponse deviceResponse) {
                    try {
                        PublicLightingGetPowerUsageHistoryRequestMessageProcessor.this.handleUnableToConnectDeviceResponse(deviceResponse, t,
                                powerUsageHistoryMessageDataContainerDto, PublicLightingGetPowerUsageHistoryRequestMessageProcessor.this.responseMessageSender,
                                deviceResponse, message.getStringProperty(Constants.DOMAIN), message.getStringProperty(Constants.DOMAIN_VERSION), message
                                        .getJMSType(), message.propertyExists(Constants.IS_SCHEDULED) ? message.getBooleanProperty(Constants.IS_SCHEDULED)
                                        : false, message.getIntProperty(Constants.RETRY_COUNT));
                    } catch (final JMSException e) {
                        LOGGER.error("JMSException", e);
                    }

                }
            };

            final GetPowerUsageHistoryDeviceRequest deviceRequest = new GetPowerUsageHistoryDeviceRequest(organisationIdentification, deviceIdentification,
                    correlationUid, powerUsageHistoryMessageDataContainerDto.getTimePeriod(), powerUsageHistoryMessageDataContainerDto.getHistoryTermType());

            this.deviceService.getPowerUsageHistory(deviceRequest, deviceResponseHandler, ipAddress);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, domain, domainVersion, messageType, retryCount);
        }
    }

    protected void handleGetPowerUsageHistoryDeviceResponse(final DeviceResponse deviceResponse, final PowerUsageHistoryMessageDataContainer messageData,
            final DeviceResponseMessageSender responseMessageSender, final String domain, final String domainVersion, final String messageType,
            final boolean isScheduled, final int retryCount) {

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        OsgpException osgpException = null;
        PowerUsageHistoryResponseMessageDataContainer powerUsageHistoryResponseMessageDataContainerDto = null;
        Serializable dataObject;

        try {
            final GetPowerUsageHistoryDeviceResponse response = (GetPowerUsageHistoryDeviceResponse) deviceResponse;
            this.deviceResponseService.handleDeviceMessageStatus(response.getStatus());
            powerUsageHistoryResponseMessageDataContainerDto = new PowerUsageHistoryResponseMessageDataContainer(response.getPowerUsageHistoryData());
            dataObject = powerUsageHistoryResponseMessageDataContainerDto;
        } catch (final Exception e) {
            LOGGER.error("Device Response Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            dataObject = messageData;
            osgpException= new TechnicalException(ComponentType.UNKNOWN, "Unexpected exception while retrieving response message", e);
        }

        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage(domain, domainVersion, messageType, deviceResponse.getCorrelationUid(),
                deviceResponse.getOrganisationIdentification(), deviceResponse.getDeviceIdentification(), result, osgpException, dataObject, isScheduled,
                retryCount);

        responseMessageSender.send(responseMessage);
    }
}
