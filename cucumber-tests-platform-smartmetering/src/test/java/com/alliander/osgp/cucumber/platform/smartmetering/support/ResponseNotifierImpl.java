/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.MeterResponseData;
import com.alliander.osgp.adapter.ws.smartmetering.domain.repositories.MeterResponseDataRepository;
import com.alliander.osgp.logging.domain.entities.DeviceLogItem;
import com.alliander.osgp.logging.domain.repositories.DeviceLogItemRepository;

@Deprecated
@Component
@Configuration
public class ResponseNotifierImpl implements ResponseNotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseNotifierImpl.class);

    private static final int FIRST_WAIT_TIME = 1000;

    @Autowired
    private DeviceLogItemRepository deviceLogItemRepository;

    @Autowired
    private MeterResponseDataRepository meterResponseDataRepository;

    @Override
    public boolean waitForResponse(final String correlationUid, final int timeout, final int maxtime) {
        if (timeout < 1) {
            throw new IllegalArgumentException("Timeout < 1 is not possible");
        }

        try {
            // check if we have (almost) immediate response
            Thread.sleep(FIRST_WAIT_TIME);
            PollResult pollResult = this.pollMeterResponseDatabase(correlationUid);

            int delayedTime = 0;
            while (true) {
                switch (pollResult) {
                case OK:
                    return true;
                case ERROR:
                    return false;
                case NOT_OK:
                    delayedTime += timeout;
                    if (delayedTime > maxtime) {
                        return false;
                    }
                }
                Thread.sleep(timeout);
                pollResult = this.pollMeterResponseDatabase(correlationUid);
            }
        } catch (final InterruptedException e) {
            LOGGER.error("Unexpected exception waiting for response", e);
            return false;
        }
    }

    @Override
    public boolean waitForLog(final String deviceId, final int timeout, final int maxtime) {
        if (timeout < 1) {
            throw new IllegalArgumentException("Timeout < 1 is not possible");
        }

        try {
            // check if we have (almost) immediate response
            Thread.sleep(FIRST_WAIT_TIME);
            PollResult pollResult = this.pollLogDatabase(deviceId);

            int delayedTime = 0;
            while (true) {
                switch (pollResult) {
                case OK:
                    return true;
                case ERROR:
                    return false;
                case NOT_OK:
                    delayedTime += timeout;
                    if (delayedTime > maxtime) {
                        return false;
                    }
                }
                Thread.sleep(timeout);
                pollResult = this.pollLogDatabase(deviceId);
            }
        } catch (final InterruptedException e) {
            LOGGER.error("Unexpected exception waiting for response", e);
            return false;
        }
    }

    private PollResult pollMeterResponseDatabase(final String correlationUid) {
        PollResult pollResult = PollResult.NOT_OK;
        try {
            final List<MeterResponseData> meterResponseDataByCorrelationUid = this.meterResponseDataRepository
                    .findByCorrelationUid(correlationUid);
            if (!meterResponseDataByCorrelationUid.isEmpty()) {
                pollResult = PollResult.OK;
            }
        } catch (final Exception e) {
            LOGGER.error("Error polling MeterResponseData for correlationUid {}", correlationUid, e);
            pollResult = PollResult.ERROR;
        }
        return pollResult;
    }

    private PollResult pollLogDatabase(final String deviceId) {
        PollResult result = PollResult.NOT_OK;
        try {
            final List<DeviceLogItem> deviceLogItems = this.deviceLogItemRepository
                    .findByDeviceIdentification(deviceId, new PageRequest(0, 2)).getContent();

            if (!deviceLogItems.isEmpty()) {
                result = PollResult.OK;
            }
        } catch (final Exception e) {
            LOGGER.error("Error polling LogData for device identification {}", deviceId, e);
            result = PollResult.ERROR;
        }
        return result;
    }

    private enum PollResult {
        OK,
        NOT_OK,
        ERROR;
    }
}
