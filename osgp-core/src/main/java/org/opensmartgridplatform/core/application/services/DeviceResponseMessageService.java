/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.application.services;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.opensmartgridplatform.core.domain.model.domain.DomainResponseService;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.ScheduledTask;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.ScheduledTaskRepository;
import org.opensmartgridplatform.domain.core.valueobjects.ScheduledTaskStatusType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ProtocolRequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

@Service
@Transactional
public class DeviceResponseMessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceResponseMessageService.class);

    // The array of exceptions which have to be retried.
    private static final String[] RETRY_EXCEPTIONS = { "Unable to connect", "ConnectException",
            "Failed to receive response within timelimit", "Timeout waiting for",
            "Connection closed by remote host while waiting for association response" };

    @Autowired
    private DomainResponseService domainResponseMessageSender;

    @Autowired
    private ScheduledTaskRepository scheduledTaskRepository;

    @Autowired
    private DeviceRequestMessageService deviceRequestMessageService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private int getMaxRetryCount;

    public void processMessage(final ProtocolResponseMessage message) {
        LOGGER.info("Processing protocol response message with correlation uid [{}]", message.getCorrelationUid());

        try {
            if (message.isScheduled() && !message.bypassRetry()) {
                LOGGER.info("Handling scheduled protocol response message.");
                this.handleScheduledTask(message);
            } else {
                LOGGER.info("Handling protocol response message.");
                this.handleProtocolResponseMessage(message);
            }
        } catch (JMSException | FunctionalException e) {
            LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
        }
    }

    /**
     * Determine if the request should be retried.
     *
     * @param message
     *            Response message.
     * @return True if result is NOT_OK and the maximum number of retries has
     *         been reached and the exception indicates a problem that might be
     *         gone when retried.
     */
    private boolean shouldRetryBasedOnMessage(final ProtocolResponseMessage message) {
        return !message.bypassRetry() && message.getResult() == ResponseMessageResultType.NOT_OK
                && message.getRetryCount() < this.getMaxRetryCount
                && this.shouldRetryBasedOnException(message.getOsgpException());
    }

    /**
     * Determine if the exception or its cause indicates a problem that might be
     * gone when retried.
     *
     * @param e
     *            OsgpException
     * @return True when exception message matches one from the RETRY_EXCEPTIONS
     *         list.
     */
    private boolean shouldRetryBasedOnException(final OsgpException e) {
        if (e == null) {
            return false;
        }

        final String exceptionMsg = e.toString();
        if (e.getCause() != null) {
            exceptionMsg.concat(e.getCause().toString());
        }

        for (final String retryException : RETRY_EXCEPTIONS) {
            if (exceptionMsg.contains(retryException)) {
                return true;
            }
        }
        return false;
    }

    private void handleScheduledTask(final ProtocolResponseMessage message) {
        final ScheduledTask scheduledTask = this.scheduledTaskRepository
                .findByCorrelationUid(message.getCorrelationUid());

        if (scheduledTask == null) {
            LOGGER.error("Scheduled task for device [{}] with correlation uid [{}] not found",
                    message.getDeviceIdentification(), message.getCorrelationUid());
            return;
        }

        if (this.messageIsSuccessful(message, scheduledTask)) {
            this.domainResponseMessageSender.send(message);
            this.scheduledTaskRepository.delete(scheduledTask);
        } else {

            this.handleUnsuccessfulScheduledTask(message, scheduledTask);
        }
    }

    private void handleUnsuccessfulScheduledTask(final ProtocolResponseMessage message,
            final ScheduledTask scheduledTask) {
        if (this.mustBeRetried(message)) {
            this.handleMessageRetry(message, scheduledTask);
        } else {
            this.domainResponseMessageSender.send(message);
            this.scheduledTaskRepository.delete(scheduledTask);
        }
    }

    private void handleMessageRetry(final ProtocolResponseMessage message, final ScheduledTask scheduledTask) {
        scheduledTask.setFailed(this.determineErrorMessage(message));
        scheduledTask.retryOn(message.getRetryHeader().getScheduledRetryTime());
        this.scheduledTaskRepository.save(scheduledTask);
    }

    private boolean mustBeRetried(final ProtocolResponseMessage message) {
        return !message.bypassRetry() && message.getRetryHeader() != null && message.getRetryHeader().shouldRetry();
    }

    private boolean messageIsSuccessful(final ProtocolResponseMessage message, final ScheduledTask scheduledTask) {
        return message.getResult() == ResponseMessageResultType.OK
                && scheduledTask.getStatus() == ScheduledTaskStatusType.PENDING;
    }

    private String determineErrorMessage(final ProtocolResponseMessage message) {
        if (message.getOsgpException() == null) {
            return "";
        } else if (message.getOsgpException().getCause() == null) {
            return message.getOsgpException().getMessage();
        } else {
            return message.getOsgpException().getCause().getMessage();
        }
    }

    private void handleProtocolResponseMessage(final ProtocolResponseMessage message)
            throws FunctionalException, JMSException {
        if (this.mustBeRetried(message)) {
            // Create scheduled task for retries.
            LOGGER.info("Creating a scheduled retry task for message of type {} for device {}.",
                    message.getMessageType(), message.getDeviceIdentification());
            final ScheduledTask task = this.createScheduledRetryTask(message);
            this.scheduledTaskRepository.save(task);
        } else if (this.shouldRetryBasedOnMessage(message)) {
            // Immediate retry based on error message. Should be deprecated.
            LOGGER.info("Retrying: {} for device {} for {} time", message.getMessageType(),
                    message.getDeviceIdentification(), message.getRetryCount() + 1);
            final ProtocolRequestMessage protocolRequestMessage = this.createProtocolRequestMessage(message);
            this.deviceRequestMessageService.processMessage(protocolRequestMessage);
        } else {
            LOGGER.info("Sending domain response message for message of type {} for device {} with correlationUid {}.",
                    message.getMessageType(), message.getDeviceIdentification(), message.getCorrelationUid());
            this.domainResponseMessageSender.send(message);
        }
    }

    private ProtocolRequestMessage createProtocolRequestMessage(final ProtocolResponseMessage message)
            throws JMSException {
        final Device device = this.deviceRepository.findByDeviceIdentification(message.getDeviceIdentification());

        final Serializable messageData = message.getDataObject();

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(message);

        return new ProtocolRequestMessage.Builder().deviceMessageMetadata(deviceMessageMetadata)
                .domain(message.getDomain()).domainVersion(message.getDomainVersion()).ipAddress(device.getIpAddress())
                .request(messageData).scheduled(message.isScheduled()).retryCount(message.getRetryCount() + 1).build();
    }

    private ScheduledTask createScheduledRetryTask(final ProtocolResponseMessage message) throws JMSException {

        final Serializable messageData = message.getDataObject();
        final Timestamp scheduleTimeStamp = new Timestamp(message.getRetryHeader().getScheduledRetryTime().getTime());

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(message);

        final ScheduledTask task = new ScheduledTask(deviceMessageMetadata, message.getDomain(),
                message.getDomainVersion(), messageData, scheduleTimeStamp);
        task.retryOn(scheduleTimeStamp);

        return task;
    }
}
