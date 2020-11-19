/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.shared.services;

import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseDataRepository;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.CorrelationUidException;
import org.opensmartgridplatform.shared.exceptionhandling.CorrelationUidMismatchException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.UnknownCorrelationUidException;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(value = "transactionManager", propagation = Propagation.REQUIRES_NEW)
public class ResponseDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseDataService.class);

    @Autowired
    private ResponseDataRepository responseDataRepository;

    /**
     * Queue response data object.
     *
     * @param responseData
     */
    public void enqueue(final ResponseData responseData) {
        if (this.responseDataRepository.findByCorrelationUid(responseData.getCorrelationUid()) == null) {
            this.responseDataRepository.save(responseData);
        } else {
            LOGGER.warn("Response data with correlation uid {} already exists. Skipping save action",
                    responseData.getCorrelationUid());
        }
    }

    /**
     * Dequeue response data.
     *
     * If correlationUid does not match type, null will be returned and the data
     * will not be removed from the database.
     *
     * @param correlationUid
     * @param expectedClassType
     *            type to check the data object against.
     * @return ReponseData Response data.
     * @throws FunctionalException
     *             when correlationUid is not found, or when the expected class
     *             type does not match.
     */
    public ResponseData dequeue(final String correlationUid, final Class<?> expectedClassType,
            final ComponentType componentType) throws CorrelationUidException {

        final ResponseData responseData = this.responseDataRepository.findByCorrelationUid(correlationUid);

        if (responseData == null) {
            LOGGER.warn("No response data for correlation UID {}", correlationUid);
            throw new UnknownCorrelationUidException(componentType);
        }

        if (!this.isValidResponseType(responseData, expectedClassType)) {
            /**
             * Return null if data is not of the expected type. The response
             * data will not be removed, so it will be available for the right
             * request type.
             */
            String warningResultClassType;
            if (responseData.getMessageData() == null) {
                warningResultClassType = "NULL";
            } else {
                warningResultClassType = responseData.getMessageData().getClass().getName();
            }

            LOGGER.warn("Incorrect type of response data: {} for correlation UID: {}", warningResultClassType,
                    responseData.getCorrelationUid());

            throw new CorrelationUidMismatchException(componentType);
        }

        this.remove(responseData);
        return responseData;
    }

    public ResponseData dequeue(final String correlationUid, final ComponentType componentType)
            throws UnknownCorrelationUidException {
        final ResponseData responseData = this.responseDataRepository.findByCorrelationUid(correlationUid);

        if (responseData == null) {
            LOGGER.warn("No response data for correlation UID {}", correlationUid);
            throw new UnknownCorrelationUidException(componentType);
        }

        this.remove(responseData);
        return responseData;
    }

    /**
     * ResponseData is valid when ResponseData message data type is equal to the
     * expected type OR The response message result type is NOT_OK, so the
     * message data will be exception information.
     *
     * @param responseData
     *            response data.
     * @param expectedClassType
     *            expected class
     * @return is valid.
     */
    private boolean isValidResponseType(final ResponseData responseData, final Class<?> expectedClassType) {
        return expectedClassType.isInstance(responseData.getMessageData()) || responseData.getResultType().equals(
                ResponseMessageResultType.NOT_OK);
    }

    private void remove(final ResponseData responseData) {
        LOGGER.info("deleting ResponseData for CorrelationUid {}", responseData.getCorrelationUid());
        this.responseDataRepository.delete(responseData);
    }
}
