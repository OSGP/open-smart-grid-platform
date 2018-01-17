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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronSequenceGenerator;

import com.alliander.osgp.adapter.ws.domain.entities.ResponseData;
import com.alliander.osgp.adapter.ws.domain.repositories.ResponseDataRepository;

public abstract class AbstractResendNotificationService {

    @Autowired
    private String resendNotificationCronExpression;

    @Autowired
    private int resendNotificationMultiplier;

    @Autowired
    private Short resendNotificationMaximum;

    @Autowired
    private ResponseDataRepository responseDataRepository;

    public void execute() {
        final List<ResponseData> notificationsToResend = this.responseDataRepository
                .findByNumberOfNotificationsSentLessThan(this.resendNotificationMaximum);
        for (final ResponseData responseData : notificationsToResend) {
            final double multiplier = Math.pow(this.resendNotificationMultiplier,
                    responseData.getNumberOfNotificationsSent());

            final Date modificationDate = responseData.getModificationTime();
            final Date currentDate = new Date();
            final long previousModificationTime = currentDate.getTime() - modificationDate.getTime();

            final CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(
                    this.resendNotificationCronExpression);
            final Date nextFirstExecutionDate = cronSequenceGenerator.next(currentDate);
            final Date nextSecondExecutionDate = cronSequenceGenerator.next(nextFirstExecutionDate);
            final long cronSequenceInterval = nextSecondExecutionDate.getTime() - nextFirstExecutionDate.getTime();

            if ((cronSequenceInterval * multiplier) < previousModificationTime) {
                this.executer(responseData);
            }
        }
    }

    public abstract void executer(ResponseData responseData);

    public String getNotificationMessage(final String responseData) {
        return String.format("Response of type %s is available.", responseData);
    }
}
