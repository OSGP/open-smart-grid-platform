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

import com.alliander.osgp.adapter.ws.domain.entities.ResponseData;
import com.alliander.osgp.adapter.ws.domain.repositories.ResponseDataRepository;

public abstract class AbstractResendNotificationService {

    @Autowired
    private int resendNotificationMultiplier;

    @Autowired
    private Short resendNotificationMaximum;

    @Autowired
    private int resendThresholdInHours;

    @Autowired
    private ResponseDataRepository responseDataRepository;

    public void execute() {
        final int initialNotificationResend = 1;
        final List<ResponseData> notificationsToResend = this.responseDataRepository
                .findByNumberOfNotificationsSentLessThan(this.resendNotificationMaximum);
        for (final ResponseData responseData : notificationsToResend) {
            final double multiplier = Math.pow(this.resendNotificationMultiplier,
                    responseData.getNumberOfNotificationsSent());

            final Date currentDate = new Date();
            final long previousModificationTimeInterval = currentDate.getTime()
                    - responseData.getModificationTime().getTime();
            final long creationTimeInterval = currentDate.getTime() - responseData.getCreationTime().getTime();

            if (TimeUnit.MINUTES.toMillis(this.resendThresholdInHours) < creationTimeInterval) {
                if (multiplier == initialNotificationResend) {
                    this.resendNotification(responseData);
                } else if ((TimeUnit.HOURS.toMillis(this.resendThresholdInHours)
                        * multiplier) < previousModificationTimeInterval) {
                    this.resendNotification(responseData);
                }
            }
        }
    }

    public abstract void resendNotification(ResponseData responseData);

    public String getNotificationMessage(final String responseData) {
        return String.format("Response of type %s is available.", responseData);
    }
}
