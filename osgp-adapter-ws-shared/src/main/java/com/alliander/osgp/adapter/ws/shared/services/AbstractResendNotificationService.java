/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.shared.services;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.alliander.osgp.adapter.ws.domain.entities.ResponseData;
import com.alliander.osgp.adapter.ws.domain.repositories.ResponseDataRepository;
import com.alliander.osgp.shared.exceptionhandling.CircuitBreakerOpenException;

public abstract class AbstractResendNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractResendNotificationService.class);

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

        final long[] resendDelays = this.calculateResendDelays(this.resendNotificationMaximum);

        /*
         * Loop through the different values from high to low. If for some
         * reason there are relatively old data this should prevent
         * notifications being sent multiple times for the same response data in
         * this single execution.
         */
        for (int notificationsResent = this.resendNotificationMaximum
                - 1; notificationsResent > -1; notificationsResent--) {
            final long delay = resendDelays[notificationsResent];
            final DateTime createdBefore = DateTime.now(DateTimeZone.UTC).minus(delay);
            /*
             * Dealing with the response data for each number of resent
             * notifications separately, allows for specification of the
             * ultimate creation date for the response data as a query
             * parameter. This way all response data returned can be handled,
             * and no further checks are needed. (Given the paged handling of
             * the results this eliminates the chance of repeatedly getting a
             * full page of response data of which none are actually re-sent.)
             */
            try {
                this.resendNotifications((short) notificationsResent, createdBefore);
            } catch (final CircuitBreakerOpenException exc) {
                LOGGER.warn(
                        "Processing notifications for this run will be stopped, because the circuit breaker is open.",
                        exc);
                break;
            }
        }
    }

    private void resendNotifications(final short notificationsResent, final DateTime createdBefore) {

        List<ResponseData> responseDataForNotifying = this.getResponseDataForNotifying(notificationsResent,
                createdBefore);

        /*
         * The getResponseData call returns at most a page size of response data
         * for which to re-send a notification (which all should have the number
         * of notifications sent equal to the value provided as method
         * parameter).
         *
         * In the process of re-sending the notification the response data
         * should be updated (number of notifications sent incremented) which
         * makes sure the same response data will never be returned again on
         * repeated calls.
         *
         * The call to getResponseData will be repeated until the resulting list
         * is empty, indicating all response data eligible for the current
         * execution of re-sending notifications have been processed.
         */
        while (!responseDataForNotifying.isEmpty()) {
            LOGGER.info(
                    "About to send {} notification(s) for response data created before {} with {} prior notification(s)",
                    responseDataForNotifying.size(), createdBefore, notificationsResent + 1);
            for (final ResponseData responseData : responseDataForNotifying) {
                this.resendNotificationAndUpdateResponseData(responseData);
            }
            responseDataForNotifying = this.getResponseDataForNotifying(notificationsResent, createdBefore);
        }
    }

    public void resendNotificationAndUpdateResponseData(final ResponseData responseData) {

        try {
            this.resendNotification(responseData);
        } finally {
            responseData.setNumberOfNotificationsSent((short) (responseData.getNumberOfNotificationsSent() + 1));
            this.responseDataRepository.save(responseData);
        }
    }

    protected void logUnknownNotificationTypeError(final String correlationUid, final String messageType,
            final String notificationServiceName) {

        LOGGER.error(
                "Unable to send notification for response data for correlation UID \"{}\" because notification type \"{}\" is not known with {}",
                correlationUid, messageType, notificationServiceName);
        /*
         * Only log the error and do not throw an exception, so the response
         * data gets a higher number of notifications sent (preventing endlessly
         * - until cleanup - repeated errors for the same response data) and
         * further processing of other response data continues.
         */
    }

    /**
     * Returns an array containing numbers of milliseconds that need to have
     * passed since the first notification of certain response data before the
     * following notification should be sent.
     * <p>
     * The element at index i of the resulting array holds the number of
     * milliseconds before now that response data having
     * {@link ResponseData#getNumberOfNotificationsSent()
     * numberOfNotificationsSent} equals i should have been created to be
     * eligible for another notification attempt.
     */
    private long[] calculateResendDelays(final int maximumNumberOfResends) {
        final long[] resendDelays = new long[maximumNumberOfResends];
        long previousDelays = 0;
        for (int i = 0; i < maximumNumberOfResends; i++) {
            final long nextDelay = this.calculateDelay(i);
            resendDelays[i] = previousDelays + nextDelay;
            previousDelays = resendDelays[i];
        }
        return resendDelays;
    }

    private long calculateDelay(final int earlierResentNotifications) {
        final long standardDelayInMillis = Duration.standardMinutes(this.resendThresholdInMinutes).getMillis();
        final long factor = (long) Math.pow(this.resendNotificationMultiplier, earlierResentNotifications);
        final long delay = standardDelayInMillis * factor;
        if (delay < 0) {
            /*
             * With regular settings, long overflows should never occur here,
             * and the calculated delay should be a sensible value.
             *
             * In case of unexpected or incorrect settings, leading to negative
             * delays, make sure further processing is terminated.
             */
            throw new AssertionError("Combination of retry parameters lead to invalid delay: resendThresholdInMinutes="
                    + this.resendThresholdInMinutes + ", resendNotificationMultiplier="
                    + this.resendNotificationMultiplier + ", number of notifications resent earlier="
                    + earlierResentNotifications + ", calculated delay (milliseconds): " + delay);
        }
        return delay;
    }

    public abstract void resendNotification(ResponseData responseData);

    public String getNotificationMessage(final String responseData) {
        return String.format("Response of type %s is available.", responseData);
    }

    /**
     * Returns a list of response data of at most a single page size (as defined
     * by {@link #resendPageSize}) items, which are the oldest response data
     * with number of notification sent equal to {@code notificationsResent} and
     * a creation time before {@code createdBefore}.
     */
    private List<ResponseData> getResponseDataForNotifying(final short notificationsResent,
            final DateTime createdBefore) {
        final Pageable pageable = new PageRequest(0, this.resendPageSize, new Sort(Direction.ASC, "creationTime"));

        return this.responseDataRepository.findByNumberOfNotificationsSentAndCreationTimeBefore(notificationsResent,
                createdBefore.toDate(), pageable);
    }
}
