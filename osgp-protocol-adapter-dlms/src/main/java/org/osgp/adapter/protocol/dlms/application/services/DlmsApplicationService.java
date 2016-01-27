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

public abstract class DlmsApplicationService {
    protected void logStart(final Logger logger, final DlmsDeviceMessageMetadata messageMetadata,
            final String methodName) {
        logger.info("{} called for device: {} for organisation: {}", methodName,
                messageMetadata.getDeviceIdentification(), messageMetadata.getOrganisationIdentification());
    }

    /**
     * The service may only throw OsgpExceptions and the cause of the exception
     * can also only be a OsgpException. This is because other layers need to
     * deserialize the exception (and the cause within it) and the Exception
     * class must be known to this layer.
     *
     * If the Exception is not an OsgpException, only the exception message will
     * be wrapped in an OsgpException and returned. This also applies to the
     * cause when it is an OsgpException.
     *
     * @param e
     * @return OsgpException
     */
    protected OsgpException ensureOsgpException(final Exception e) {

        if (e instanceof OsgpException) {
            final Throwable cause = e.getCause();
            if (cause != null && !(cause instanceof OsgpException)) {
                return new OsgpException(ComponentType.PROTOCOL_DLMS, e.getMessage(), new OsgpException(
                        ComponentType.PROTOCOL_DLMS, cause.getMessage()));
            }

            return (OsgpException) e;
        }

        return new TechnicalException(ComponentType.PROTOCOL_DLMS,
                "Unexpected exception while handling protocol request/response message", new OsgpException(
                        ComponentType.PROTOCOL_DLMS, e.getMessage()));
    }

    protected void sendResponseMessage(final DlmsDeviceMessageMetadata messageMetadata,
            final ResponseMessageResultType result, final OsgpException osgpException,
            final DeviceResponseMessageSender responseMessageSender, final Serializable responseObject) {

        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage(messageMetadata.getDomain(),
                messageMetadata.getDomainVersion(), messageMetadata.getMessageType(),
                messageMetadata.getCorrelationUid(), messageMetadata.getOrganisationIdentification(),
                messageMetadata.getDeviceIdentification(), result, osgpException, responseObject,
                messageMetadata.getRetryCount());

        responseMessageSender.send(responseMessage);
    }

    protected void sendResponseMessage(final DlmsDeviceMessageMetadata messageMetadata,
            final ResponseMessageResultType result, final OsgpException osgpException,
            final DeviceResponseMessageSender responseMessageSender) {

        this.sendResponseMessage(messageMetadata, result, osgpException, responseMessageSender, null);
    }

}
