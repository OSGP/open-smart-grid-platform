/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.domain.entities.ResponseData;
import com.alliander.osgp.adapter.ws.endpoint.WebserviceEndpoint;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.shared.services.ResponseDataService;
import com.alliander.osgp.adapter.ws.shared.services.ResponseUrlService;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.exceptionhandling.UnknownCorrelationUidException;

abstract class SmartMeteringEndpoint implements WebserviceEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringEndpoint.class);

    @Autowired
    protected ResponseUrlService responseUrlService;

    @Autowired
    protected ResponseDataService responseDataService;

    /**
     * Rethrow exception if it already is a functional or technical exception,
     * otherwise throw new technical exception.
     *
     * @param e
     *            cause
     * @throws OsgpException
     */
    @Override
    public void handleException(final Exception e) throws OsgpException {
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

    protected void throwExceptionIfResultNotOk(final ResponseData meterResponseData, final String exceptionContext)
            throws OsgpException {
        if (OsgpResultType.NOT_OK == OsgpResultType.fromValue(meterResponseData.getResultType().getValue())) {
            if (meterResponseData.getMessageData() instanceof String) {
                throw new TechnicalException(ComponentType.WS_SMART_METERING,
                        (String) meterResponseData.getMessageData(), null);
            } else if (meterResponseData.getMessageData() instanceof OsgpException) {
                throw (OsgpException) meterResponseData.getMessageData();
            } else {
                throw new TechnicalException(ComponentType.WS_SMART_METERING,
                        String.format("An exception occurred %s.", exceptionContext), null);
            }
        }
    }

    @Override
    public void saveResponseUrlIfNeeded(final String correlationUid, final String responseUrl) {
        this.responseUrlService.saveResponseUrlIfNeeded(correlationUid, responseUrl);
    }

}
