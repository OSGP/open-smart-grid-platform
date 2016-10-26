/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.microgrids.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.ws.microgrids.domain.entities.RtuResponseData;
import com.alliander.osgp.adapter.ws.microgrids.domain.repositories.RtuResponseDataRepository;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.CorrelationUidException;
import com.alliander.osgp.shared.exceptionhandling.CorrelationUidMismatchException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.UnknownCorrelationUidException;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service
@Transactional(transactionManager = "wsTransactionManager")
public class RtuResponseDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RtuResponseDataService.class);

    @Autowired
    private RtuResponseDataRepository responseDataRepository;

    /**
     * Queue response data object.
     *
     * @param responseData
     */
    public void enqueue(final RtuResponseData responseData) {
        // Check if response data is already present.
        // This might happen when the message processor is retrying to process a
        // queue message.
        final RtuResponseData existing = this.responseDataRepository
                .findSingleResultByCorrelationUid(responseData.getCorrelationUid());
        if (existing == null) {
            this.responseDataRepository.save(responseData);
        } else {
            LOGGER.warn("Response data with correlation uid {} already present. Skipping save action",
                    responseData.getCorrelationUid());
        }
    }

    /**
     * Dequeue rtu response data.
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
    public RtuResponseData dequeue(final String correlationUid, final Class<?> expectedClassType)
            throws CorrelationUidException {

        final RtuResponseData responseData = this.responseDataRepository
                .findSingleResultByCorrelationUid(correlationUid);

        if (responseData == null) {
            LOGGER.warn("No response data for correlation UID {}", correlationUid);
            throw new UnknownCorrelationUidException(ComponentType.WS_MICROGRIDS);
        }

        if (!this.isValidResponseType(responseData, expectedClassType)) {
            /**
             * Return null if data is not of the expected type. The Rtu response
             * data will not be removed, so it will be available for the right
             * request type.
             */
            final String warningResultClassType = responseData.getMessageData() == null ? "NULL"
                    : responseData.getMessageData().getClass().getName();

            LOGGER.warn("Incorrect type of response data: {} for correlation UID: {}", warningResultClassType,
                    responseData.getCorrelationUid());

            throw new CorrelationUidMismatchException(ComponentType.WS_MICROGRIDS);
        }

        this.remove(responseData);
        return responseData;
    }

    public RtuResponseData dequeue(final String correlationUid) throws UnknownCorrelationUidException {
        final RtuResponseData responseData = this.responseDataRepository
                .findSingleResultByCorrelationUid(correlationUid);

        if (responseData == null) {
            LOGGER.warn("No response data for correlation UID {}", correlationUid);
            throw new UnknownCorrelationUidException(ComponentType.WS_MICROGRIDS);
        }

        this.remove(responseData);
        return responseData;
    }

    /**
     * RtuResponseData is valid when RtuResponseData message data type is equal
     * to the expected type OR The response message result type is NOT_OK, so
     * the message data will be exception information.
     *
     * @param meterResponseData
     *            meter response data.
     * @param expectedClassType
     *            expected class
     * @return is valid.
     */
    private boolean isValidResponseType(final RtuResponseData meterResponseData, final Class<?> expectedClassType) {
        return expectedClassType.isInstance(meterResponseData.getMessageData())
                || meterResponseData.getResultType().equals(ResponseMessageResultType.NOT_OK);
    }

    private void remove(final RtuResponseData meterResponseData) {
        LOGGER.info("deleting MeterResponseData for CorrelationUid {}", meterResponseData.getCorrelationUid());
        this.responseDataRepository.delete(meterResponseData);
    }
}
