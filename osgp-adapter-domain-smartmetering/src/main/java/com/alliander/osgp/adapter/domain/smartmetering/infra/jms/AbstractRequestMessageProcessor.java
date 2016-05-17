package com.alliander.osgp.adapter.domain.smartmetering.infra.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

public class AbstractRequestMessageProcessor {


    @Autowired
    protected WebServiceResponseMessageSender webServiceResponseMessageSender;

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRequestMessageProcessor.class);
    
    /**
     * In case of an error, this function can be used to send a response
     * containing the exception to the web-service-adapter.
     *
     * @param e
     *            The exception
     * @param deviceMessageMetadata
     *            The {@link DeviceMessageMetadata}
     */
    protected void handleError(final Exception e, final DeviceMessageMetadata deviceMessageMetadata) {
        LOGGER.info("handeling error: {} for message type: {}", e.getMessage(), deviceMessageMetadata.getMessageType());
        final OsgpException osgpException = this.ensureOsgpException(e);
        
        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                ResponseMessageResultType.NOT_OK, osgpException, null, deviceMessageMetadata.getMessagePriority()),
                deviceMessageMetadata.getMessageType());
    }

    private OsgpException ensureOsgpException(final Exception e) {

        if (e instanceof OsgpException) {
            return (OsgpException) e;
        }

        return new TechnicalException(ComponentType.DOMAIN_SMART_METERING,
                "Unexpected exception while retrieving message", e);
    }
}
