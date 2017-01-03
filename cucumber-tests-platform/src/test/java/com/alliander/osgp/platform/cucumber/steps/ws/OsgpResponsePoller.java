/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.platform.cucumber.support.ws.WebServiceSecurityException;

public abstract class OsgpResponsePoller<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OsgpResponsePoller.class);

    private final int maxWaitTimeForResponse;
    private final int sleepTime;

    public OsgpResponsePoller(final int maxWaitTimeForResponse, final int sleepTime) {
        this.maxWaitTimeForResponse = maxWaitTimeForResponse;
        this.sleepTime = sleepTime;
    }

    abstract protected T pollWsResponse() throws WebServiceSecurityException, GeneralSecurityException, IOException;

    /**
     * Polls OSGP for response availability for a max time.
     *
     * @return the actual ws response object
     * @throws InterruptedException
     *             if interrupted during sleep
     * @throws WebServiceSecurityException
     *             if an unexpected ws exception occurs
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public T start() throws InterruptedException, WebServiceSecurityException, GeneralSecurityException, IOException {

        int timeSlept = 0;

        while (timeSlept < this.maxWaitTimeForResponse) {
            try {
                final T responseObject = this.pollWsResponse();
                return responseObject;
            } catch (final SoapFaultClientException e) {
                if ("CorrelationUid is unknown.".equals(e.getMessage())) {
                    LOGGER.warn("CorrelationId is not available yet");
                } else {
                    LOGGER.error("Unexpected exception", e);
                    throw e;
                }

                Thread.sleep(this.sleepTime);
                timeSlept += this.sleepTime;
            }

        }
        throw new AssertionError("Correlation Uid is not available in time. Time slept is " + timeSlept);
    }
}
