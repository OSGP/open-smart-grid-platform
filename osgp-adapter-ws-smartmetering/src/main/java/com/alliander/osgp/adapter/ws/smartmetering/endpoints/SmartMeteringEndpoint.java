package com.alliander.osgp.adapter.ws.smartmetering.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.exceptionhandling.UnknownCorrelationUidException;

abstract class SmartMeteringEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringEndpoint.class);

    /**
     * Rethrow exception if it already is a functional or technical exception,
     * otherwise throw new technical exception.
     *
     * @param e
     *            cause
     * @throws OsgpException
     */
    protected OsgpException handleException(final Exception e) {
        if (e instanceof OsgpException) {
            if (e instanceof UnknownCorrelationUidException) {
                LOGGER.warn(e.getMessage());
            } else {
                LOGGER.error("Exception occurred: ", e);
            }
            return (OsgpException) e;
        } else {
            LOGGER.error("Exception occurred: ", e);
            return new TechnicalException(ComponentType.WS_SMART_METERING, e);
        }
    }
}
