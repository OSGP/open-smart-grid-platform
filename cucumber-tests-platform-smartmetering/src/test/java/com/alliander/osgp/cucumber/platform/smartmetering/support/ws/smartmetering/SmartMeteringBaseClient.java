/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alliander.osgp.adapter.ws.domain.repositories.ResponseDataRepository;
import com.alliander.osgp.cucumber.platform.support.ws.BaseClient;

public abstract class SmartMeteringBaseClient extends BaseClient {

    @Autowired
    private ResponseDataRepository responseDataRepository;

    @Value("${smartmetering.response.wait.check.interval:1000}")
    private int waitCheckIntervalMillis;
    @Value("${smartmetering.response.wait.fail.duration:30000}")
    private int waitFailMillis;

    public void setWaitFailMillis(final int waitFailMillis) {
        this.waitFailMillis = waitFailMillis;
    }

    protected void waitForDlmsResponseData(final String correlationUid) {
        /*
         * Polling the database is a temporary implementation of waiting for a response
         * in the tests. It is considered better than polling the platforms web service
         * layer, but the preferable solution is to be able to respond to the
         * notification sent by the platform, and not to poll external resources at all.
         */
        try {
            for (int timeSpentWaiting = 0; timeSpentWaiting < this.waitFailMillis; timeSpentWaiting += this.waitCheckIntervalMillis) {
                Thread.sleep(this.waitCheckIntervalMillis);
                if (this.responseDataRepository.findByCorrelationUid(correlationUid) != null) {
                    return;
                }
            }
            throw new AssertionError(
                    "MeterResponseData not available within " + this.waitFailMillis + " milliseconds.");
        } catch (final InterruptedException e) {
            throw new AssertionError("Waiting for MeterResponseData was interrupted.", e);
        }
    }
}
