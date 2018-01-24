/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.shared.services;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.alliander.osgp.adapter.ws.domain.entities.ResponseData;
import com.alliander.osgp.adapter.ws.domain.repositories.ResponseDataRepository;

public abstract class AbstractResendNotificationService {

    @Autowired
    private int resendNotificationMultiplier;

    @Autowired
    private Short resendNotificationMaximum;

    @Autowired
    private int resendThresholdInMinutes;

    @Autowired
    private ResponseDataRepository responseDataRepository;

    @Autowired
    private int resendPageSize;

    public void execute() {

        List<ResponseData> notificationsToResend = this.getResponseData();

        while (!notificationsToResend.isEmpty()) {
            for (final ResponseData responseData : notificationsToResend) {
                if (this.nextNotificationTime(responseData) < new Date().getTime()) {
                    this.resendNotification(responseData);
                }
            }
            notificationsToResend = this.getResponseData();
        }
    }

    private long nextNotificationTime(final ResponseData responseData) {
        final long modificationTime = responseData.getModificationTime().getTime();
        final long resendThresholdTime = TimeUnit.MINUTES.toMillis(this.resendThresholdInMinutes);
        final long multiplier = (long) Math.pow(this.resendNotificationMultiplier,
                responseData.getNumberOfNotificationsSent());
        return ((modificationTime) + (resendThresholdTime * multiplier));
    }

    public abstract void resendNotification(ResponseData responseData);

    public String getNotificationMessage(final String responseData) {
        return String.format("Response of type %s is available.", responseData);
    }

    private List<ResponseData> getResponseData() {
        final Pageable pageable = new PageRequest(0, this.resendPageSize);

        return this.responseDataRepository.findByNumberOfNotificationsSentLessThan(this.resendNotificationMaximum,
                pageable);
    }
}
