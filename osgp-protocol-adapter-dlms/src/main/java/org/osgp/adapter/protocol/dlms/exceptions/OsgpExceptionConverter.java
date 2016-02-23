package org.osgp.adapter.protocol.dlms.exceptions;

import org.springframework.stereotype.Component;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

/**
 * OsgpExceptionConverter
 *
 * Converts given exception to a OsgpException type, and removes cause
 * exceptions from any other type. This is because other layers need to
 * deserialize the exception (and the cause within it) and the Exception class
 * must be known to these layers.
 *
 */
@Component
public class OsgpExceptionConverter {

    /**
     * If the Exception is a OsgpException, this exception is returned.
     *
     * If the Exception is not an OsgpException, only the exception message will
     * be wrapped in an TechnicalException (OsgpException subclass) and
     * returned. This also applies to the cause when it is an OsgpException.
     *
     * @param e
     *            The exception.
     * @return OsgpException the given exception or a new TechnicalException
     *         instance.
     */
    public OsgpException ensureOsgpOrTechnicalException(final Exception e) {
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
}
