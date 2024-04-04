// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import jakarta.jms.JMSException;
import java.io.Serializable;
import java.util.function.Consumer;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SystemEventService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.throttling.ThrottlingService;
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
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage.Builder;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
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

  @Autowired protected ThrottlingService throttlingService;

  @Autowired private SystemEventService systemEventService;

  @Autowired private MessagePriorityHandler messagePriorityHandler;

  public void createAndHandleConnectionForDevice(
      final DlmsDevice device,
      final MessageMetadata messageMetadata,
      final Consumer<DlmsConnectionManager> taskForConnectionManager)
      throws OsgpException {

    final Permit permit =
        this.throttlingService.requestPermit(
            messageMetadata.getBaseTransceiverStationId(),
            messageMetadata.getCellId(),
            messageMetadata.getMessagePriority());

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
      this.throttlingService.releasePermit(permit);
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

    this.throttlingService.releasePermit(conn.getPermit());

    if (device.needsInvocationCounter()) {
      this.updateInvocationCounterForDevice(device, conn);

      this.systemEventService.verifySystemEventThresholdReachedEvent(device, messageMetadata);
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
    this.deviceRepository.updateInvocationCounter(
        device.getDeviceIdentification(), device.getInvocationCounter());
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

    final Builder messageBuilder =
        new Builder().messageMetadata(messageMetadata).result(result).dataObject(responseObject);

    if (exception != null) {
      messageBuilder.osgpException(
          this.osgpExceptionConverter.ensureOsgpOrTechnicalException(exception));
    }

    if (this.shouldRetry(result, exception, responseObject)) {
      messageBuilder.retryHeader(
          this.retryHeaderFactory.createRetryHeader(messageMetadata.getRetryCount()));
      messageBuilder.messagePriority(
          this.messagePriorityHandler.recalculatePriority(messageMetadata));
    }

    responseMessageSender.send(messageBuilder.build());
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
