/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import java.io.Serializable;
import java.util.function.Consumer;
import javax.jms.JMSException;
import org.opensmartgridplatform.adapter.protocol.dlms.application.config.ThrottlingClientConfig;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SystemEventService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ThrottlingService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionTaskException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NonRetryableException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.OsgpExceptionConverter;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.infra.jms.RetryHeader;
import org.opensmartgridplatform.throttling.api.Permit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract base class for message processors dealing with optional DlmsConnection creation and
 * DlmsMessageListener handling.
 */
public abstract class DlmsConnectionMessageProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DlmsConnectionMessageProcessor.class);

  @Autowired protected DlmsConnectionHelper dlmsConnectionHelper;

  @Autowired protected DlmsLogItemRequestMessageSender dlmsLogItemRequestMessageSender;

  @Autowired protected OsgpExceptionConverter osgpExceptionConverter;

  @Autowired private RetryHeaderFactory retryHeaderFactory;

  @Autowired protected DlmsDeviceRepository deviceRepository;

  @Autowired(required = false)
  protected ThrottlingService throttlingService;

  @Autowired protected ThrottlingClientConfig throttlingClientConfig;

  @Autowired private SystemEventService systemEventService;

  public void createAndHandleConnectionForDevice(
      final DlmsDevice device,
      final MessageMetadata messageMetadata,
      final Consumer<DlmsConnectionManager> taskForConnectionManager)
      throws OsgpException {

    Permit permit = null;
    if (this.throttlingClientConfig.clientEnabled()) {
      permit =
          this.throttlingClientConfig
              .throttlingClient()
              .requestPermitUsingNetworkSegmentIfIdsAreAvailable(
                  messageMetadata.getBaseTransceiverStationId(), messageMetadata.getCellId());
    } else {
      this.throttlingService.openConnection();
    }

    final DlmsMessageListener dlmsMessageListener =
        this.createMessageListenerForDeviceConnection(device, messageMetadata);

    try {
      this.dlmsConnectionHelper.createAndHandleConnectionForDevice(
          messageMetadata, device, dlmsMessageListener, permit, taskForConnectionManager);
    } catch (final ConnectionTaskException e) {
      /*
       * Exceptions thrown by the tasks working with the DlmsConnectionManager are wrapped in
       * ConnectionTaskException in the ThrowingConsumer.
       */
      throw e.getOsgpException();
    } catch (final Exception e) {
      /*
       * Throttling permit is released in this catch block to make sure that it is made available
       * again when there were problems setting up the connection and only then (exceptions thrown
       * by the tasks working with the DlmsConnectionManager are wrapped as ConnectionTaskException
       * and are caught in the previous catch block, so they don't end up here).
       *
       * If there are no problems in setting up the connection, releasing the throttling permit is
       * the responsibility of the tasks for the DlmsConnectionManager, as seen in
       * DeviceRequestMessageProcessor.processMessageTasks(), where
       * this.doConnectionPostProcessing() is called in a finally block.
       */
      if (this.throttlingClientConfig.clientEnabled()) {
        this.throttlingClientConfig.throttlingClient().releasePermit(permit);
      } else {
        this.throttlingService.closeConnection();
      }
      throw e;
    }
  }

  protected DlmsMessageListener createMessageListenerForDeviceConnection(
      final DlmsDevice device, final MessageMetadata messageMetadata) {
    final InvocationCountingDlmsMessageListener dlmsMessageListener;
    if (device.isInDebugMode()) {
      dlmsMessageListener =
          new LoggingDlmsMessageListener(
              device.getDeviceIdentification(), this.dlmsLogItemRequestMessageSender);
      dlmsMessageListener.setMessageMetadata(messageMetadata);
      dlmsMessageListener.setDescription("Create connection");
    } else if (device.needsInvocationCounter()) {
      dlmsMessageListener = new InvocationCountingDlmsMessageListener();
    } else {
      dlmsMessageListener = null;
    }
    return dlmsMessageListener;
  }

  protected void doConnectionPostProcessing(
      final DlmsDevice device,
      final DlmsConnectionManager conn,
      final MessageMetadata messageMetadata) {
    if (conn == null) {
      /*
       * No connection (possible and perfectly valid if an operation was handled that
       * did not involve device communication), then no follow-up actions are
       * required.
       */
      return;
    }

    this.setClosingDlmsConnectionMessageListener(device, conn);

    if (this.throttlingClientConfig.clientEnabled()) {
      this.throttlingClientConfig.throttlingClient().releasePermit(conn.getPermit());
    } else {
      this.throttlingService.closeConnection();
    }

    if (device.needsInvocationCounter()) {
      final boolean invocationCounterLowered =
          this.systemEventService.receivedInvocationCounterIsLowerThanCurrentValue(
              device, messageMetadata, conn);
      if (!invocationCounterLowered) {
        this.updateInvocationCounterForDevice(device, conn);
        this.systemEventService.verifySystemEventThresholdReachedEvent(device, messageMetadata);
      }
    }
  }

  protected void setClosingDlmsConnectionMessageListener(
      final DlmsDevice device, final DlmsConnectionManager conn) {
    LOGGER.info("Closing connection with {}", device.getDeviceIdentification());
    final DlmsMessageListener dlmsMessageListener = conn.getDlmsMessageListener();
    dlmsMessageListener.setDescription("Close connection");
  }

  /* package private */
  void updateInvocationCounterForDevice(final DlmsDevice device, final DlmsConnectionManager conn) {
    if (!(conn.getDlmsMessageListener() instanceof InvocationCountingDlmsMessageListener)) {
      LOGGER.error(
          "updateInvocationCounterForDevice should only be called for devices with HLS 5 "
              + "communication with an InvocationCountingDlmsMessageListener - device: {}, hls5: {}, "
              + "listener: {}",
          device.getDeviceIdentification(),
          device.isHls5Active(),
          conn.getDlmsMessageListener() == null
              ? "null"
              : conn.getDlmsMessageListener().getClass().getName());
      return;
    }

    final InvocationCountingDlmsMessageListener dlmsMessageListener =
        (InvocationCountingDlmsMessageListener) conn.getDlmsMessageListener();
    final int numberOfSentMessages = dlmsMessageListener.getNumberOfSentMessages();
    device.incrementInvocationCounter(numberOfSentMessages);
    this.deviceRepository.save(device);
  }
  /**
   * @param logger the logger from the calling subClass
   * @param exception the exception to be logged
   * @param messageMetadata a DlmsMessageMetadata containing debug info to be logged
   */
  protected void logJmsException(
      final Logger logger, final JMSException exception, final MessageMetadata messageMetadata) {
    logger.error(
        "UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", exception);
    logger.debug("correlationUid: {}", messageMetadata.getCorrelationUid());
    logger.debug("domain: {}", messageMetadata.getDomain());
    logger.debug("domainVersion: {}", messageMetadata.getDomainVersion());
    logger.debug("messageType: {}", messageMetadata.getMessageType());
    logger.debug("organisationIdentification: {}", messageMetadata.getOrganisationIdentification());
    logger.debug("deviceIdentification: {}", messageMetadata.getDeviceIdentification());
  }

  protected void assertRequestObjectType(final Class<?> expected, final Serializable requestObject)
      throws ProtocolAdapterException {
    if (!expected.isInstance(requestObject)) {
      throw new ProtocolAdapterException(
          String.format(
              "The request object has an incorrect type. %s expected but %s was found.",
              expected.getCanonicalName(), requestObject.getClass().getCanonicalName()));
    }
  }

  protected void sendResponseMessage(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType result,
      final Exception exception,
      final DeviceResponseMessageSender responseMessageSender,
      final Serializable responseObject) {

    OsgpException osgpException = null;
    if (exception != null) {
      osgpException = this.osgpExceptionConverter.ensureOsgpOrTechnicalException(exception);
    }

    final RetryHeader retryHeader;
    if (this.shouldRetry(result, exception, responseObject)) {
      retryHeader = this.retryHeaderFactory.createRetryHeader(messageMetadata.getRetryCount());
    } else {
      retryHeader = this.retryHeaderFactory.createEmptyRetryHeader();
    }

    final ProtocolResponseMessage responseMessage =
        new ProtocolResponseMessage.Builder()
            .messageMetadata(messageMetadata)
            .result(result)
            .osgpException(osgpException)
            .dataObject(responseObject)
            .retryHeader(retryHeader)
            .build();

    responseMessageSender.send(responseMessage);
  }

  /* suppress unused parameter warning, because we need it in override method */
  @SuppressWarnings("java:S1172")
  protected boolean shouldRetry(
      final ResponseMessageResultType result,
      final Exception exception,
      final Serializable responseObject) {
    return result == ResponseMessageResultType.NOT_OK
        && !(exception instanceof NonRetryableException);
  }
}
