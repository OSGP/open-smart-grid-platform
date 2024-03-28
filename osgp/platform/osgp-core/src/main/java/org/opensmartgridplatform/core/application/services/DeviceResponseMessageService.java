// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.application.services;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import org.opensmartgridplatform.core.domain.model.domain.DomainResponseService;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.ScheduledTask;
import org.opensmartgridplatform.domain.core.valueobjects.ScheduledTaskStatusType;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ProtocolRequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceResponseMessageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceResponseMessageService.class);

  // The array of exceptions which have to be retried.
  private static final String[] RETRY_EXCEPTIONS = {
    "Unable to connect",
    "ConnectException",
    "Failed to receive response within timelimit",
    "Timeout waiting for",
    "Connection closed by remote host while waiting for association response"
  };

  @Autowired private DeviceService deviceService;

  @Autowired private DomainResponseService domainResponseMessageSender;

  @Autowired private ScheduledTaskService scheduledTaskService;

  @Autowired private DeviceRequestMessageService deviceRequestMessageService;

  @Autowired private DeviceCommunicationInformationService deviceCommunicationInformationService;

  @Autowired private int getMaxRetryCount;

  public void processMessage(final ProtocolResponseMessage message) {
    LOGGER.info(
        "Processing protocol response message with correlation uid [{}]",
        message.getCorrelationUid());

    this.deviceCommunicationInformationService.updateDeviceConnectionInformation(message);

    try {
      if (message.isScheduled() && !message.bypassRetry()) {
        LOGGER.info("Handling scheduled protocol response message.");
        this.handleScheduledTask(message);
      } else {
        LOGGER.info("Handling protocol response message.");
        this.handleProtocolResponseMessage(message);
      }
    } catch (final FunctionalException e) {
      LOGGER.error(
          "Exception processing protocol response message with correlation uid [{}]",
          message.getCorrelationUid(),
          e);
      if (FunctionalExceptionType.MAX_SCHEDULE_TIME_EXCEEDED == e.getExceptionType()) {
        this.handleMaxScheduleTimeExceeded(message, e);
      }
    }
  }

  /**
   * Determine if the request should be retried.
   *
   * @param message Response message.
   * @return True if result is NOT_OK and the maximum number of retries has been reached and the
   *     exception indicates a problem that might be gone when retried.
   */
  private boolean shouldRetryBasedOnMessage(final ProtocolResponseMessage message) {
    return !message.bypassRetry()
        && message.getResult() == ResponseMessageResultType.NOT_OK
        && message.getRetryCount() < this.getMaxRetryCount
        && this.shouldRetryBasedOnException(message.getOsgpException());
  }

  /**
   * Determine if the exception or its cause indicates a problem that might be gone when retried.
   *
   * @param e OsgpException
   * @return True when exception message matches one from the RETRY_EXCEPTIONS list.
   */
  private boolean shouldRetryBasedOnException(final OsgpException e) {
    if (e == null) {
      return false;
    }

    String exceptionMsg = e.toString();
    if (e.getCause() != null) {
      exceptionMsg = exceptionMsg.concat(e.getCause().toString());
    }

    for (final String retryException : RETRY_EXCEPTIONS) {
      if (exceptionMsg.contains(retryException)) {
        return true;
      }
    }
    return false;
  }

  private void handleScheduledTask(final ProtocolResponseMessage message)
      throws FunctionalException {

    final ScheduledTask scheduledTask =
        this.scheduledTaskService.findByCorrelationUid(message.getCorrelationUid());

    if (scheduledTask == null) {
      LOGGER.error(
          "Scheduled task for device [{}] with correlation uid [{}] not found",
          message.getDeviceIdentification(),
          message.getCorrelationUid());
      return;
    }

    if (this.messageIsSuccessful(message, scheduledTask)) {
      this.domainResponseMessageSender.send(message);
      this.scheduledTaskService.deleteScheduledTask(scheduledTask);
    } else {
      this.handleUnsuccessfulScheduledTask(message, scheduledTask);
    }
  }

  private void handleUnsuccessfulScheduledTask(
      final ProtocolResponseMessage message, final ScheduledTask scheduledTask)
      throws FunctionalException {

    final boolean allowRetryAttempt = !this.alreadyHasMaxScheduleTimeExceededException(message);
    if (allowRetryAttempt && this.mustBeRetried(message)) {
      this.handleMessageRetry(message, scheduledTask);
    } else {
      this.domainResponseMessageSender.send(message);
      this.scheduledTaskService.deleteScheduledTask(scheduledTask);
    }
  }

  private boolean alreadyHasMaxScheduleTimeExceededException(
      final ProtocolResponseMessage message) {

    if (!(message.getOsgpException() instanceof FunctionalException)) {
      return false;
    }
    final FunctionalException functionalException =
        (FunctionalException) message.getOsgpException();
    return FunctionalExceptionType.MAX_SCHEDULE_TIME_EXCEEDED
        == functionalException.getExceptionType();
  }

  private void handleMessageRetry(
      final ProtocolResponseMessage message, final ScheduledTask scheduledTask)
      throws FunctionalException {

    final Date scheduledRetryTime = message.getRetryHeader().getScheduledRetryTime();
    final Long maxScheduleTime = message.getMaxScheduleTime();

    if (this.timeForRetryExceedsMaxScheduleTime(scheduledRetryTime, maxScheduleTime)) {
      this.scheduledTaskService.deleteScheduledTask(scheduledTask);
      throw new FunctionalException(
          FunctionalExceptionType.MAX_SCHEDULE_TIME_EXCEEDED,
          ComponentType.OSGP_CORE,
          message.getOsgpException());
    }

    scheduledTask.setFailed(this.determineErrorMessage(message));
    scheduledTask.retryOn(scheduledRetryTime);
    scheduledTask.setMessagePriority(message.getMessagePriority());
    this.scheduledTaskService.saveScheduledTask(scheduledTask);
  }

  private boolean timeForRetryExceedsMaxScheduleTime(
      final Date scheduledRetryTime, final Long maxScheduleTime) {

    if (maxScheduleTime == null) {
      // No max schedule time that can be exceeded for any actual schedule time.
      return false;
    }

    if (System.currentTimeMillis() > maxScheduleTime) {
      /*
       *  No need to check the scheduledRetryTime, as it will never result in a message being
       *  scheduled and executed before maxScheduleTime, since the latter is already in the past.
       */
      return true;
    }

    return scheduledRetryTime.getTime() > maxScheduleTime;
  }

  private boolean mustBeRetried(final ProtocolResponseMessage message) {
    return !message.bypassRetry()
        && message.getRetryHeader() != null
        && message.getRetryHeader().shouldRetry();
  }

  private boolean messageIsSuccessful(
      final ProtocolResponseMessage message, final ScheduledTask scheduledTask) {
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
      throws FunctionalException {

    final boolean allowRetryAttempt = !this.alreadyHasMaxScheduleTimeExceededException(message);
    if (allowRetryAttempt && this.mustBeRetried(message)) {
      // Create scheduled task for retries.
      LOGGER.info(
          "Creating a scheduled retry task for message of type {} for device {}.",
          message.getMessageType(),
          message.getDeviceIdentification());
      final ScheduledTask task = this.createScheduledRetryTask(message);
      this.scheduledTaskService.saveScheduledTask(task);
    } else if (allowRetryAttempt && this.shouldRetryBasedOnMessage(message)) {
      // Immediate retry based on error message. Should be deprecated.
      LOGGER.info(
          "Retrying: {} for device {} for {} time",
          message.getMessageType(),
          message.getDeviceIdentification(),
          message.getRetryCount() + 1);
      final ProtocolRequestMessage protocolRequestMessage =
          this.createProtocolRequestMessage(message);
      this.deviceRequestMessageService.processMessage(protocolRequestMessage);
    } else {
      LOGGER.info(
          "Sending domain response message for message of type {} for device {} with correlationUid {}.",
          message.getMessageType(),
          message.getDeviceIdentification(),
          message.getCorrelationUid());
      this.domainResponseMessageSender.send(message);
    }
  }

  private ProtocolRequestMessage createProtocolRequestMessage(final ProtocolResponseMessage message)
      throws FunctionalException {

    final Date scheduledRetryTime = new Date(); // retry is performed now, with the created message
    final Long maxScheduleTime = message.getMaxScheduleTime();
    if (this.timeForRetryExceedsMaxScheduleTime(scheduledRetryTime, maxScheduleTime)) {
      throw new FunctionalException(
          FunctionalExceptionType.MAX_SCHEDULE_TIME_EXCEEDED,
          ComponentType.OSGP_CORE,
          message.getOsgpException());
    }

    final Device device =
        this.deviceService.findByDeviceIdentification(message.getDeviceIdentification());

    final Serializable messageData = message.getDataObject();

    return ProtocolRequestMessage.newBuilder()
        .messageMetadata(
            message
                .messageMetadata()
                .builder()
                .withNetworkAddress(getNetworkAddress(device))
                .withNetworkSegmentIds(device.getBtsId(), device.getCellId())
                .withRetryCount(message.getRetryCount() + 1)
                .build())
        .request(messageData)
        .build();
  }

  private static String getNetworkAddress(final Device device) {
    if (device.getNetworkAddress() == null && device.getGatewayDevice() != null) {
      return device.getGatewayDevice().getNetworkAddress();
    }
    return device.getNetworkAddress();
  }

  private ScheduledTask createScheduledRetryTask(final ProtocolResponseMessage message)
      throws FunctionalException {

    final Date scheduledRetryTime = message.getRetryHeader().getScheduledRetryTime();
    final Long maxScheduleTime = message.getMaxScheduleTime();

    if (this.timeForRetryExceedsMaxScheduleTime(scheduledRetryTime, maxScheduleTime)) {
      throw new FunctionalException(
          FunctionalExceptionType.MAX_SCHEDULE_TIME_EXCEEDED,
          ComponentType.OSGP_CORE,
          message.getOsgpException());
    }

    final Serializable messageData = message.getDataObject();
    final Timestamp scheduleTimeStamp = new Timestamp(scheduledRetryTime.getTime());

    final MessageMetadata messageMetadata = message.messageMetadata();

    final ScheduledTask task =
        new ScheduledTask(
            messageMetadata,
            message.getDomain(),
            message.getDomainVersion(),
            messageData,
            scheduleTimeStamp);
    task.retryOn(scheduleTimeStamp);

    return task;
  }

  private void handleMaxScheduleTimeExceeded(
      final ProtocolResponseMessage message,
      final FunctionalException maxScheduleTimeExceededException) {

    final FunctionalException originalOsgpException =
        this.getOriginalOsgpException(message, maxScheduleTimeExceededException);

    final ProtocolResponseMessage maxScheduleTimeExceededResponseMessage =
        ProtocolResponseMessage.newBuilder()
            .messageMetadata(message.messageMetadata())
            .result(ResponseMessageResultType.NOT_OK)
            .osgpException(originalOsgpException)
            .dataObject(message.getDataObject())
            .build();
    this.domainResponseMessageSender.send(maxScheduleTimeExceededResponseMessage);
  }

  private FunctionalException getOriginalOsgpException(
      final ProtocolResponseMessage message, final FunctionalException functionalException) {
    if (functionalException.getExceptionType() == FunctionalExceptionType.MAX_SCHEDULE_TIME_EXCEEDED
        && message.getOsgpException() != null
        && message.getOsgpException() instanceof final FunctionalException originalException) {
      return originalException;
    } else {
      return functionalException;
    }
  }
}
