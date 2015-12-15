package org.osgp.adapter.protocol.dlms.application.services;

import java.io.Serializable;

import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsDeviceMessageMetadata;
import org.slf4j.Logger;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

public class DlmsApplicationService {
    protected void logStart(final Logger logger, final DlmsDeviceMessageMetadata messageMetadata,
            final String methodName) {
        logger.info("{} called for device: {} for organisation: {}", methodName,
                messageMetadata.getDeviceIdentification(), messageMetadata.getOrganisationIdentification());
    }

    protected OsgpException ensureOsgpException(final Exception e) {

        if (e instanceof OsgpException) {
            return (OsgpException) e;
        }

        return new TechnicalException(ComponentType.PROTOCOL_DLMS,
                "Unexpected exception while handling protocol request/response message", e);
    }

    protected void sendResponseMessage(final DlmsDeviceMessageMetadata messageMetadata,
            final ResponseMessageResultType result, final OsgpException osgpException,
            final DeviceResponseMessageSender responseMessageSender, final Serializable responseObject) {

        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage(messageMetadata.getDomain(),
                messageMetadata.getDomainVersion(), messageMetadata.getMessageType(),
                messageMetadata.getCorrelationUid(), messageMetadata.getOrganisationIdentification(),
                messageMetadata.getDeviceIdentification(), result, osgpException, responseObject);

        responseMessageSender.send(responseMessage);
    }

    protected void sendResponseMessage(final DlmsDeviceMessageMetadata messageMetadata,
            final ResponseMessageResultType result, final OsgpException osgpException,
            final DeviceResponseMessageSender responseMessageSender) {

        this.sendResponseMessage(messageMetadata, result, osgpException, responseMessageSender, null);
    }

}
