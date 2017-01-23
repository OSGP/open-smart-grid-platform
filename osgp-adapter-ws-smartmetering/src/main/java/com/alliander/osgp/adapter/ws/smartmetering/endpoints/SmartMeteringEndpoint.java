package com.alliander.osgp.adapter.ws.smartmetering.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.shared.services.ResponseUrlService;
import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.MeterResponseData;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.exceptionhandling.UnknownCorrelationUidException;

abstract class SmartMeteringEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringEndpoint.class);

    @Autowired
    protected ResponseUrlService responseUrlService;

    /**
     * Rethrow exception if it already is a functional or technical exception,
     * otherwise throw new technical exception.
     *
     * @param e
     *            cause
     * @throws OsgpException
     */
    protected void handleException(final Exception e) throws OsgpException {
        if (e instanceof OsgpException) {
            if (e instanceof UnknownCorrelationUidException) {
                LOGGER.warn(e.getMessage());
            } else {
                LOGGER.error("Exception occurred: ", e);
            }
            throw (OsgpException) e;
        } else {
            LOGGER.error("Exception occurred: ", e);
            throw new TechnicalException(ComponentType.WS_SMART_METERING, e);
        }
    }

    protected void throwExceptionIfResultNotOk(final MeterResponseData meterResponseData, final String exceptionContext)
            throws OsgpException {
        if (OsgpResultType.NOT_OK == OsgpResultType.fromValue(meterResponseData.getResultType().getValue())) {
            if (meterResponseData.getMessageData() instanceof String) {
                throw new TechnicalException(ComponentType.WS_SMART_METERING,
                        (String) meterResponseData.getMessageData(), null);
            } else {
                throw new TechnicalException(ComponentType.WS_SMART_METERING, String.format(
                        "An exception occurred %s.", exceptionContext), null);
            }
        }
    }

    protected void saveResponseUrlIfNeeded(final String correlationUid, final String responseUrl) {
        this.responseUrlService.saveResponseUrlIfNeeded(correlationUid, responseUrl);
    }

}
