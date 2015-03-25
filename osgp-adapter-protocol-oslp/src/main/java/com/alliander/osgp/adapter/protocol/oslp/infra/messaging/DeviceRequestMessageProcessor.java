package com.alliander.osgp.adapter.protocol.oslp.infra.messaging;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceResponse;
import com.alliander.osgp.adapter.protocol.oslp.device.responses.EmptyDeviceResponse;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.DeviceService;
import com.alliander.osgp.adapter.protocol.oslp.services.DeviceResponseService;
import com.alliander.osgp.shared.infra.jms.MessageProcessor;
import com.alliander.osgp.shared.infra.jms.MessageProcessorMap;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;
import com.alliander.osgp.shared.infra.jms.ResponseMessageSender;

/**
 * Base class for MessageProcessor implementations. Each MessageProcessor
 * implementation should be annotated with @Component. Further the MessageType
 * the MessageProcessor implementation can process should be passed in at
 * construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 * 
 * @author CGI
 * 
 */
public abstract class DeviceRequestMessageProcessor implements MessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRequestMessageProcessor.class);

    @Autowired
    protected DeviceService deviceService;

    @Autowired
    protected DeviceResponseMessageSender responseMessageSender;

    @Autowired
    protected DeviceResponseService deviceResponseService;

    @Autowired
    @Qualifier("protocolOslpDeviceRequestMessageProcessorMap")
    protected MessageProcessorMap oslpRequestMessageProcessorMap;

    protected final DeviceRequestMessageType deviceRequestMessageType;

    /**
     * Each MessageProcessor should register it's MessageType at construction.
     * 
     * @param deviceRequestMessageType
     *            The MessageType the MessageProcessor implementation can
     *            process.
     */
    protected DeviceRequestMessageProcessor(final DeviceRequestMessageType deviceRequestMessageType) {
        this.deviceRequestMessageType = deviceRequestMessageType;
    }

    /**
     * Initialization function executed after dependency injection has finished.
     * The MessageProcessor Singleton is added to the HashMap of
     * MessageProcessors. The key for the HashMap is the integer value of the
     * enumeration member.
     */
    @PostConstruct
    public void init() {
        this.oslpRequestMessageProcessorMap.addMessageProcessor(this.deviceRequestMessageType.ordinal(),
                this.deviceRequestMessageType.name(), this);
    }

    // TODO: change these two methods handleEmptyDeviceResponse and
    // handleScheduledEmptyDeviceResponse to have less double code

    protected void handleEmptyDeviceResponse(final DeviceResponse deviceResponse,
            final ResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType, final int retryCount) {

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        String description = "";

        try {
            final EmptyDeviceResponse response = (EmptyDeviceResponse) deviceResponse;
            this.deviceResponseService.handleDeviceMessageStatus(response.getStatus());
        } catch (final Exception e) {
            LOGGER.error("Device Response Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            description = e.getMessage();
        }

        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage(domain, domainVersion, messageType,
                deviceResponse.getCorrelationUid(), deviceResponse.getOrganisationIdentification(),
                deviceResponse.getDeviceIdentification(), result, description, null, retryCount);

        responseMessageSender.send(responseMessage);
    }

    protected void handleScheduledEmptyDeviceResponse(final DeviceResponse deviceResponse,
            final ResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType, final boolean isScheduled, final int retryCount) {

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        String description = "";

        try {
            final EmptyDeviceResponse response = (EmptyDeviceResponse) deviceResponse;
            this.deviceResponseService.handleDeviceMessageStatus(response.getStatus());
        } catch (final Exception e) {
            LOGGER.error("Device Response Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            description = e.getMessage();
        }

        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage(domain, domainVersion, messageType,
                deviceResponse.getCorrelationUid(), deviceResponse.getOrganisationIdentification(),
                deviceResponse.getDeviceIdentification(), result, description, null, isScheduled, retryCount);

        responseMessageSender.send(responseMessage);
    }

    protected void handleError(final Exception e, final String correlationUid, final String organisationIdentification,
            final String deviceIdentification, final String domain, final String domainVersion,
            final String messageType, final int retryCount) {
        LOGGER.error("Error while processing message", e);

        final ProtocolResponseMessage protocolResponseMessage = new ProtocolResponseMessage(domain, domainVersion,
                messageType, correlationUid, organisationIdentification, deviceIdentification,
                ResponseMessageResultType.NOT_OK, e.getMessage(), null, retryCount);

        this.responseMessageSender.send(protocolResponseMessage);
    }

    protected void handleError(final Exception e, final String correlationUid, final String organisationIdentification,
            final String deviceIdentification, final String domain, final String domainVersion, final String messageType) {
        LOGGER.error("Error while processing message", e);

        final ProtocolResponseMessage protocolResponseMessage = new ProtocolResponseMessage(domain, domainVersion,
                messageType, correlationUid, organisationIdentification, deviceIdentification,
                ResponseMessageResultType.NOT_OK, e.getMessage(), null);

        this.responseMessageSender.send(protocolResponseMessage);
    }

    public void handleUnableToConnectDeviceResponse(final DeviceResponse deviceResponse, final Throwable t,
            final Serializable messageData, final DeviceResponseMessageSender responseMessageSender,
            final DeviceResponse deviceResponse2, final String domain, final String domainVersion,
            final String messageType, final boolean isScheduled, final int retryCount) {

        final ResponseMessageResultType result = ResponseMessageResultType.NOT_OK;
        final String description = t.getMessage();

        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage(domain, domainVersion, messageType,
                deviceResponse.getCorrelationUid(), deviceResponse.getOrganisationIdentification(),
                deviceResponse.getDeviceIdentification(), result, description, messageData, isScheduled, retryCount);

        this.responseMessageSender.send(responseMessage);
    }
}
