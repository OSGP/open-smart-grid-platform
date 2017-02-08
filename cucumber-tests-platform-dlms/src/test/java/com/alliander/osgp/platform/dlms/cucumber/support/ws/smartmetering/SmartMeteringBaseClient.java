/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.support.ws.smartmetering;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alliander.osgp.adapter.ws.smartmetering.domain.repositories.MeterResponseDataRepository;
import com.alliander.osgp.platform.cucumber.support.ws.BaseClient;

public abstract class SmartMeteringBaseClient extends BaseClient {

    @Autowired
    private MeterResponseDataRepository meterResponseDataRepository;

    @Value("${smartmetering.response.wait.check.interval:1000}")
    private int waitCheckIntervalMillis;
    @Value("${smartmetering.response.wait.fail.duration:30000}")
    private int waitFailMillis;

    protected void waitForDlmsResponseData(final String correlationUid) {
        try {
            for (int timeSpentWaiting = 0; timeSpentWaiting < this.waitFailMillis; timeSpentWaiting += this.waitCheckIntervalMillis) {
                Thread.sleep(this.waitCheckIntervalMillis);
                if (this.meterResponseDataRepository.findSingleResultByCorrelationUid(correlationUid) != null) {
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
