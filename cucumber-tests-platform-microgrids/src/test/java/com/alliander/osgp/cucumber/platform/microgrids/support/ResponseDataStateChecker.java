/**
 * Copyright 2017 Smart Society Services B.V.
 */
package com.alliander.osgp.cucumber.platform.microgrids.support;

import static org.junit.Assert.fail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.ws.microgrids.domain.entities.RtuResponseData;
import com.alliander.osgp.adapter.ws.microgrids.domain.repositories.RtuResponseDataRepository;

@Component
public class RtuResponseDataStateChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(RtuResponseDataStateChecker.class);

    @Autowired
    private RtuResponseDataRepository rtuResponseDataRepository;

    public void waitForRtuResponseDataToBeRemoved(final String correlationUid, final int timeout, final int maxtime) {

        try {
            for (int delayedtime = 0; delayedtime < maxtime; delayedtime += timeout) {
                Thread.sleep(timeout);

                final RtuResponseData rtuResponseData = this.rtuResponseDataRepository
                        .findSingleResultByCorrelationUid(correlationUid);

                if (rtuResponseData == null) {
                    return;
                }
            }
        } catch (final InterruptedException e) {
            LOGGER.error("Thread sleep interrupted ", e.getMessage());
        }
        fail("Rtu response data with correlation uid " + correlationUid + " should be removed within: " + maxtime
                + "sec.");
    }

    public void waitToMakeSureRtuResponseDataIsNotRemoved(final String correlationUid, final int timeout,
            final int maxtime) {

        try {
            for (int delayedtime = 0; delayedtime < maxtime; delayedtime += timeout) {
                Thread.sleep(timeout);

                final RtuResponseData rtuResponseData = this.rtuResponseDataRepository
                        .findSingleResultByCorrelationUid(correlationUid);
                if (rtuResponseData == null) {
                    fail("Rtu response data with correlation uid " + correlationUid + " should not be removed.");
                }

            }
        } catch (final InterruptedException e) {
            LOGGER.error("Thread sleep interrupted ", e.getMessage());
        }
    }

}
