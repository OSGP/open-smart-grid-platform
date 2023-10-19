// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import java.util.Objects;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ThrowingConsumer;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsLogItemRequestMessageSender;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.InvocationCountingDlmsMessageListener;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.LoggingDlmsMessageListener;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.throttling.api.Permit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Object that manages initializing and resetting the value of the invocationCounter property of a
 * device.
 */
@Component
public class InvocationCounterManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(InvocationCounterManager.class);
  private static final AttributeAddress ATTRIBUTE_ADDRESS_INVOCATION_COUNTER_VALUE =
      new AttributeAddress(1, new ObisCode(new byte[] {0, 0, 43, 1, 0, -1}), 2);

  private final DlmsConnectionFactory connectionFactory;
  private final DlmsHelper dlmsHelper;
  private final DlmsDeviceRepository deviceRepository;
  private final DlmsLogItemRequestMessageSender dlmsLogItemRequestMessageSender;

  @Autowired
  public InvocationCounterManager(
      final DlmsConnectionFactory connectionFactory,
      final DlmsHelper dlmsHelper,
      final DlmsDeviceRepository deviceRepository,
      final DlmsLogItemRequestMessageSender dlmsLogItemRequestMessageSender) {
    this.connectionFactory = connectionFactory;
    this.dlmsHelper = dlmsHelper;
    this.deviceRepository = deviceRepository;
    this.dlmsLogItemRequestMessageSender = dlmsLogItemRequestMessageSender;
  }

  /**
   * Updates the device instance with the invocation counter value on the actual device. Should only
   * be called for a device that actually has an invocation counter stored on the device itself.
   */
  public void initializeInvocationCounter(
      final MessageMetadata messageMetadata, final DlmsDevice device) throws OsgpException {
    /*
     * When the invocation counter is out of sync, the device closes the session.
     * By setting the ip-address to null, the application will be forced to get a new ip-address.
     * The meter will start a new session.
     * This is done by the DlmsConnectionFactory in the method:
     * this.domainHelperService.setIpAddressFromMessageMetadataOrSessionProvider
     */
    device.setIpAddress(null);

    this.initializeWithInvocationCounterStoredOnDevice(messageMetadata, device, null);

    /*
     * When the invocation counter has been initialized, the device closes the session.
     * By setting the ip-address to null, the application will be forced to get a new ip-address.
     * The meter will start a new session.
     * This is done by the DlmsConnectionFactory in the method:
     * this.domainHelperService.setIpAddressFromMessageMetadataOrSessionProvider
     */
    device.setIpAddress(null);
  }

  /**
   * Updates the device instance with the invocation counter value on the actual device. Should only
   * be called for a device that actually has an invocation counter stored on the device itself. If
   * a permit for network access is passed, it is to be released upon closing the connection.
   */
  private void initializeWithInvocationCounterStoredOnDevice(
      final MessageMetadata messageMetadata, final DlmsDevice device, final Permit permit)
      throws OsgpException {

    final ThrowingConsumer<DlmsConnectionManager> taskForConnectionManager =
        connectionManager ->
            this.initializeWithInvocationCounterStoredOnDeviceTask(device, connectionManager);

    final DlmsMessageListener dlmsMessageListener =
        this.createMessageListenerForDeviceConnection(device, messageMetadata);

    this.connectionFactory.createAndHandlePublicClientConnection(
        messageMetadata, device, dlmsMessageListener, permit, taskForConnectionManager);
  }

  void initializeWithInvocationCounterStoredOnDeviceTask(
      final DlmsDevice device, final DlmsConnectionManager connectionManager)
      throws FunctionalException {

    final Long previousKnownInvocationCounter = device.getInvocationCounter();
    final Long invocationCounterFromDevice = this.getInvocationCounter(connectionManager);
    if (Objects.equals(previousKnownInvocationCounter, invocationCounterFromDevice)) {
      LOGGER.warn(
          "Initializing invocationCounter of device {} with the value that was already known: {}",
          device.getDeviceIdentification(),
          previousKnownInvocationCounter);
    } else if (invocationCounterFromDevice < previousKnownInvocationCounter) {
      LOGGER.error(
          "Attempt to lower invocationCounter of device {}", device.getDeviceIdentification());
      throw new FunctionalException(
          FunctionalExceptionType.ATTEMPT_TO_LOWER_INVOCATION_COUNTER, ComponentType.PROTOCOL_DLMS);
    } else {
      /*
       * To prevent optimistic locking failures if the device gets saved again
       * (for instance from updateInvocationCounterForDevice, called from
       * doConnectionPostProcessing in DlmsConnectionMessageProcessor)
       * we update the invocation counter in a query.
       */
      device.setInvocationCounter(invocationCounterFromDevice);
      this.deviceRepository.updateInvocationCounter(
          device.getDeviceIdentification(), device.getInvocationCounter());
      LOGGER.info(
          "Property invocationCounter of device {} initialized to the value of the invocation counter "
              + "stored on the device: {}{}",
          device.getDeviceIdentification(),
          device.getInvocationCounter(),
          previousKnownInvocationCounter == null
              ? ""
              : " (previous known value: " + previousKnownInvocationCounter + ")");
    }
  }

  private long getInvocationCounter(final DlmsConnectionManager connectionManager)
      throws FunctionalException {
    final Number invocationCounter =
        this.dlmsHelper
            .getAttributeValue(connectionManager, ATTRIBUTE_ADDRESS_INVOCATION_COUNTER_VALUE)
            .getValue();
    return invocationCounter.longValue();
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
    } else {
      dlmsMessageListener = null;
    }
    return dlmsMessageListener;
  }
}
