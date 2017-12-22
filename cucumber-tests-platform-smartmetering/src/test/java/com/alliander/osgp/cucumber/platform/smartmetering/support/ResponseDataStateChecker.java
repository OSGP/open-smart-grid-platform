/**
 * Copyright 2017 Smart Society Services B.V.
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support;

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

                final ResponseData responseData = this.responseDataRepository
                        .findSingleResultByCorrelationUid(correlationUid);

                if (responseData == null) {
                    return;
                }
            }
        } catch (final InterruptedException e) {
            LOGGER.error("Thread sleep interrupted ", e.getMessage());
        }
        fail("Meter response data with correlation uid " + correlationUid + " should be removed within: " + maxtime
                + "sec.");
    }

    public void waitToMakeSureResponseDataIsNotRemoved(final String correlationUid, final int timeout,
            final int maxtime) {

        try {
            for (int delayedtime = 0; delayedtime < maxtime; delayedtime += timeout) {
                Thread.sleep(timeout);

                final ResponseData responseData = this.responseDataRepository
                        .findSingleResultByCorrelationUid(correlationUid);
                if (responseData == null) {
                    fail("Meter response data with correlation uid " + correlationUid + " should not be removed.");
                }

            }
        } catch (final InterruptedException e) {
            LOGGER.error("Thread sleep interrupted ", e.getMessage());
        }
    }

}
