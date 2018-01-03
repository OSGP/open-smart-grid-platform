/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.microgrids.support;

import static org.junit.Assert.fail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.ws.domain.entities.ResponseData;
import com.alliander.osgp.adapter.ws.domain.repositories.ResponseDataRepository;

@Component
public class ResponseDataStateChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseDataStateChecker.class);

    @Autowired
    private ResponseDataRepository responseDataRepository;

    public void waitForResponseDataToBeRemoved(final String correlationUid, final int timeout, final int maxtime) {

        try {
            for (int delayedtime = 0; delayedtime < maxtime; delayedtime += timeout) {
                Thread.sleep(timeout);

                final ResponseData responseData = this.responseDataRepository.findByCorrelationUid(correlationUid);

                if (responseData == null) {
                    return;
                }
            }
        } catch (final InterruptedException e) {
            LOGGER.error("Thread sleep interrupted ", e.getMessage());
        }
        fail("Rtu response data with correlation uid " + correlationUid + " should be removed within: " + maxtime
                + "sec.");
    }

    public void waitToMakeSureResponseDataIsNotRemoved(final String correlationUid, final int timeout,
            final int maxtime) {

        try {
            for (int delayedtime = 0; delayedtime < maxtime; delayedtime += timeout) {
                Thread.sleep(timeout);

                final ResponseData responseData = this.responseDataRepository.findByCorrelationUid(correlationUid);
                if (responseData == null) {
                    fail("Rtu response data with correlation uid " + correlationUid + " should not be removed.");
                }

            }
        } catch (final InterruptedException e) {
            LOGGER.error("Thread sleep interrupted ", e.getMessage());
        }
    }

}
