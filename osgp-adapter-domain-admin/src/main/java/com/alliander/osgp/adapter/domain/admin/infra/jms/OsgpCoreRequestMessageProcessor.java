package com.alliander.osgp.adapter.domain.admin.infra.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.UnknownMessageTypeException;

@Component(value = "domainAdminIncomingOsgpCoreRequestMessageProcessor")
public class OsgpCoreRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(OsgpCoreRequestMessageProcessor.class);

    public void processMessage(final RequestMessage requestMessage, final String messageType)
            throws UnknownMessageTypeException {

        final String organisationIdentification = requestMessage.getOrganisationIdentification();
        final String deviceIdentification = requestMessage.getDeviceIdentification();
        final String correlationUid = requestMessage.getCorrelationUid();
        final Object dataObject = requestMessage.getRequest();

        LOGGER.info(
                "Received request message from OSGP-CORE messageType: {} deviceIdentification: {}, organisationIdentification: {}, correlationUid: {}, className: {}",
                messageType, deviceIdentification, organisationIdentification, correlationUid, dataObject.getClass()
                        .getCanonicalName());

        switch (messageType) {

            default:
                throw new UnknownMessageTypeException("Unknown JMSType: " + messageType);
        }
    }
}
