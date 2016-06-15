/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.MeterResponseData;
import com.alliander.osgp.adapter.ws.smartmetering.domain.repositories.MeterResponseDataRepository;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.CorrelationUidException;
import com.alliander.osgp.shared.exceptionhandling.CorrelationUidMismatchException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.UnknownCorrelationUidException;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service
public class MeterResponseDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeterResponseDataService.class);

    @Autowired
    private MeterResponseDataRepository meterResponseDataRepository;

    /**
     * Queue response data object.
     *
     * @param meterResponseData
     */
    public void enqueue(final MeterResponseData meterResponseData) {

        if (this.meterResponseDataRepository.findSingleResultByCorrelationUid(meterResponseData.getCorrelationUid()) == null) {
            this.meterResponseDataRepository.save(meterResponseData);
        } else {
            LOGGER.warn(
                    "Not saving meter repsonse data, because it already exists for correlationUid: {} already exists.",
                    meterResponseData.getCorrelationUid());
        }
    }

    /**
     * Dequeue meter response data.
     *
     * If correlationUid does not match type, null will be returned and the data
     * will not be removed from the database.
     *
     * @param correlationUid
     * @param expectedClassType
     *            type to check the data object against.
     * @return MeterReponseData Response data.
     * @throws FunctionalException
     *             when correlationUid is not found, or when the expected class
     *             type does not match.
     */
    public MeterResponseData dequeue(final String correlationUid, final Class<?> expectedClassType)
            throws CorrelationUidException {

        final MeterResponseData meterResponseData = this.meterResponseDataRepository
                .findSingleResultByCorrelationUid(correlationUid);

        if (meterResponseData == null) {
            LOGGER.warn("No response data for correlation UID {}", correlationUid);
            throw new UnknownCorrelationUidException(ComponentType.WS_SMART_METERING);
        }

        if (!this.isValidResponseType(meterResponseData, expectedClassType)) {
            /**
             * Return null if data is not of the expected type. The Meter
             * response data will not be removed, so it will be available for
             * the right request type.
             */
            final String warningResultClassType = meterResponseData.getMessageData() == null ? "NULL"
                    : meterResponseData.getMessageData().getClass().getName();

            LOGGER.warn("Incorrect type of response data: {} for correlation UID: {}", warningResultClassType,
                    meterResponseData.getCorrelationUid());

            throw new CorrelationUidMismatchException(ComponentType.WS_SMART_METERING);
        }

        this.remove(meterResponseData);
        return meterResponseData;
    }

    public MeterResponseData dequeue(final String correlationUid) throws UnknownCorrelationUidException {
        final MeterResponseData meterResponseData = this.meterResponseDataRepository
                .findSingleResultByCorrelationUid(correlationUid);

        if (meterResponseData == null) {
            LOGGER.warn("No response data for correlation UID {}", correlationUid);
            throw new UnknownCorrelationUidException(ComponentType.WS_SMART_METERING);
        }

        this.remove(meterResponseData);
        return meterResponseData;
    }

    /**
     * MeterResponseData is valid when MeterResponseData message data type is
     * equal to the expected type OR The response message result type is NOT_OK,
     * so the message data will be exception information.
     *
     * @param meterResponseData
     *            meter response data.
     * @param expectedClassType
     *            expected class
     * @return is valid.
     */
    private boolean isValidResponseType(final MeterResponseData meterResponseData, final Class<?> expectedClassType) {
        return expectedClassType.isInstance(meterResponseData.getMessageData())
                || meterResponseData.getResultType().equals(ResponseMessageResultType.NOT_OK);
    }

    private void remove(final MeterResponseData meterResponseData) {
        LOGGER.info("deleting MeterResponseData for CorrelationUid {}", meterResponseData.getCorrelationUid());
        this.meterResponseDataRepository.delete(meterResponseData);
    }
}
