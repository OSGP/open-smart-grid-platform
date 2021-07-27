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
import javax.jms.JMSException;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ThrottlingService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NonRetryableException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.OsgpExceptionConverter;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.infra.jms.RetryHeader;
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

  @Autowired protected ThrottlingService throttlingService;

  public DlmsConnectionManager createConnectionForDevice(
      final DlmsDevice device, final MessageMetadata messageMetadata) throws OsgpException {

    this.throttlingService.openConnection();

    final DlmsMessageListener dlmsMessageListener =
        this.createMessageListenerForDeviceConnection(device, messageMetadata);

    try {
      return this.dlmsConnectionHelper.createConnectionForDevice(device, dlmsMessageListener);
    } catch (final Exception e) {
      this.throttlingService.closeConnection();
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
      final DlmsDevice device, final DlmsConnectionManager conn) {
    if (conn == null) {
      /*
       * No connection (possible and perfectly valid if an operation was handled that
       * did not involve device communication), then no follow-up actions are
       * required.
       */
      return;
    }

    this.closeDlmsConnection(device, conn);

    this.throttlingService.closeConnection();

    if (device.needsInvocationCounter()) {
      this.updateInvocationCounterForDevice(device, conn);
    }
  }

  protected void closeDlmsConnection(final DlmsDevice device, final DlmsConnectionManager conn) {
    LOGGER.info("Closing connection with {}", device.getDeviceIdentification());
    final DlmsMessageListener dlmsMessageListener = conn.getDlmsMessageListener();
    dlmsMessageListener.setDescription("Close connection");
    try {
      conn.close();
    } catch (final Exception e) {
      LOGGER.error("Error while closing connection", e);
    }
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
      retryHeader = this.retryHeaderFactory.createEmtpyRetryHeader();
    }

    final ProtocolResponseMessage responseMessage =
        new ProtocolResponseMessage.Builder()
            .messageMetadata(messageMetadata)
            .domain(messageMetadata.getDomain())
            .domainVersion(messageMetadata.getDomainVersion())
            .result(result)
            .osgpException(osgpException)
            .dataObject(responseObject)
            .retryCount(messageMetadata.getRetryCount())
            .retryHeader(retryHeader)
            .scheduled(messageMetadata.isScheduled())
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
