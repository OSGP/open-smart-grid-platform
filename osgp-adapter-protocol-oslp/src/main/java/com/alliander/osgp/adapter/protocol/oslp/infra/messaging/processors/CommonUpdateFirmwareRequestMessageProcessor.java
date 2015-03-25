package com.alliander.osgp.adapter.protocol.oslp.infra.messaging.processors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceResponse;
import com.alliander.osgp.adapter.protocol.oslp.device.DeviceResponseHandler;
import com.alliander.osgp.adapter.protocol.oslp.device.FirmwareLocation;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.UpdateFirmwareDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.DeviceRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.DeviceRequestMessageType;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.DeviceService;
import com.alliander.osgp.adapter.protocol.oslp.services.DeviceResponseService;
import com.alliander.osgp.shared.infra.jms.Constants;

/**
 * Class for processing common update firmware request messages
 * 
 * @author CGI
 * 
 */
@Component("oslpCommonUpdateFirmwareRequestMessageProcessor")
public class CommonUpdateFirmwareRequestMessageProcessor extends DeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUpdateFirmwareRequestMessageProcessor.class);

    public CommonUpdateFirmwareRequestMessageProcessor() {
        super(DeviceRequestMessageType.UPDATE_FIRMWARE);
    }

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceResponseService deviceResponseService;

    @Autowired
    private FirmwareLocation firmwareLocation;

    // TODO: the FirmwareLocation class in domain and dto can/must be deleted!
    // Or, this
    // setup has to be changed in order to reuse the FirmwareLocation class in
    // the domain!!

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing common update firmware request message");

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
            final String firmwareIdentification = (String) message.getObject();

            LOGGER.info("Calling DeviceService function: {} for domain: {} {}", messageType, domain, domainVersion);

            final DeviceResponseHandler deviceResponseHandler = new DeviceResponseHandler() {

                @Override
                public void handleResponse(final DeviceResponse deviceResponse) {
                    try {
                        CommonUpdateFirmwareRequestMessageProcessor.this.handleScheduledEmptyDeviceResponse(deviceResponse,
                                CommonUpdateFirmwareRequestMessageProcessor.this.responseMessageSender, message.getStringProperty(Constants.DOMAIN), message
                                        .getStringProperty(Constants.DOMAIN_VERSION), message.getJMSType(),
                                message.propertyExists(Constants.IS_SCHEDULED) ? message.getBooleanProperty(Constants.IS_SCHEDULED) : false, message
                                        .getIntProperty(Constants.RETRY_COUNT));
                    } catch (final JMSException e) {
                        LOGGER.error("JMSException", e);
                    }

                }

                @Override
                public void handleException(final Throwable t, final DeviceResponse deviceResponse) {
                    try {
                        CommonUpdateFirmwareRequestMessageProcessor.this.handleUnableToConnectDeviceResponse(deviceResponse, t, null,
                                CommonUpdateFirmwareRequestMessageProcessor.this.responseMessageSender, deviceResponse,
                                message.getStringProperty(Constants.DOMAIN), message.getStringProperty(Constants.DOMAIN_VERSION), message.getJMSType(),
                                message.propertyExists(Constants.IS_SCHEDULED) ? message.getBooleanProperty(Constants.IS_SCHEDULED) : false,
                                message.getIntProperty(Constants.RETRY_COUNT));
                    } catch (final JMSException e) {
                        LOGGER.error("JMSException", e);
                    }

                }
            };

            final UpdateFirmwareDeviceRequest deviceRequest = new UpdateFirmwareDeviceRequest(organisationIdentification, deviceIdentification, correlationUid,
                    this.firmwareLocation.getDomain(), this.firmwareLocation.getFullPath(firmwareIdentification));

            this.deviceService.updateFirmware(deviceRequest, deviceResponseHandler, ipAddress);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, domain, domainVersion, messageType, retryCount);
        }
    }

    // TODO: method added for testing, make this a protected method
    public void setFirmwareLocation(final FirmwareLocation firmwareLocation) {
        this.firmwareLocation = firmwareLocation;
    }
}
