/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering;

import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.platform.cucumber.support.ws.WebServiceSecurityException;

public abstract class PollingReader {

    private final int maxTimeResponseAvailability;

    public PollingReader(final int maxTimeResponseAvailability) {
        this.maxTimeResponseAvailability = maxTimeResponseAvailability;
    }

    abstract protected Object getResponsePolling() throws WebServiceSecurityException;

    public Object run() throws InterruptedException, WebServiceSecurityException {

        int timeSlept = 0;

        while (timeSlept < this.maxTimeResponseAvailability) {
            try {
                final Object o = this.getResponsePolling();
                System.out.println("Object o without exception");
                return o;
            } catch (final SoapFaultClientException e) {
                System.out.println("Let try again " + e.getMessage());
                Thread.sleep(100);
                timeSlept += 100;
                System.out.println("############################### Slept: " + timeSlept);
            }

        }
        throw new AssertionError("Correlation Uid is not available in time");
    }
}
