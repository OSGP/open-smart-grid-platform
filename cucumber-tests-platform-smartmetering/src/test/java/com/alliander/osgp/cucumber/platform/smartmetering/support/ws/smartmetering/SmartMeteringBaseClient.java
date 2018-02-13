/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.notification.NotificationService;
import com.alliander.osgp.cucumber.platform.support.ws.BaseClient;

public abstract class SmartMeteringBaseClient extends BaseClient {

    @Autowired
    private NotificationService notificationService;

    @Value("${smartmetering.response.wait.check.interval:1000}")
    private int waitCheckIntervalMillis;
    @Value("${smartmetering.response.wait.fail.duration:30000}")
    private int waitFailMillis;

    public void setWaitFailMillis(final int waitFailMillis) {
        this.waitFailMillis = waitFailMillis;
    }

    protected void waitForNotification(final String correlationUid) {
        try {
            this.notificationService.getNotification(correlationUid).get(this.waitFailMillis, TimeUnit.MILLISECONDS);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError(
                    "Thread was interrupted while awaiting notification for correlation UID: " + correlationUid, e);
        } catch (final ExecutionException e) {
            throw new AssertionError("Exception while obtaining notification for correlation UID: " + correlationUid,
                    e);
        } catch (final TimeoutException e) {
            throw new AssertionError("Notification for correlation UID " + correlationUid + " not received within "
                    + this.waitFailMillis + " milliseconds.", e);
        }
    }
}
