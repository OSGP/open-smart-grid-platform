/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import java.util.Objects;
import java.util.function.Consumer;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
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

  @Autowired
  public InvocationCounterManager(
      final DlmsConnectionFactory connectionFactory, final DlmsHelper dlmsHelper) {
    this.connectionFactory = connectionFactory;
    this.dlmsHelper = dlmsHelper;
  }

  /**
   * Updates the device instance with the invocation counter value on the actual device. Should only
   * be called for a device that actually has an invocation counter stored on the device itself.
   */
  public void initializeInvocationCounter(
      final MessageMetadata messageMetadata, final DlmsDevice device) throws OsgpException {
    this.initializeWithInvocationCounterStoredOnDevice(messageMetadata, device);
  }

  private void initializeWithInvocationCounterStoredOnDevice(
      final MessageMetadata messageMetadata, final DlmsDevice device) throws OsgpException {
    final Long previousKnownInvocationCounter = device.getInvocationCounter();
    final Consumer<DlmsConnectionManager> taskForConnectionManager =
        connectionManager -> {
          try {
            final Long invocationCounterFromDevice = this.getInvocationCounter(connectionManager);
            if (Objects.equals(previousKnownInvocationCounter, invocationCounterFromDevice)) {
              LOGGER.warn(
                  "Initializing invocationCounter of device {} with the value that was already known: {}",
                  device.getDeviceIdentification(),
                  previousKnownInvocationCounter);
            } else {
              device.setInvocationCounter(invocationCounterFromDevice);
              LOGGER.info(
                  "Property invocationCounter of device {} initialized to the value of the invocation counter "
                      + "stored on the device: {}{}",
                  device.getDeviceIdentification(),
                  device.getInvocationCounter(),
                  previousKnownInvocationCounter == null
                      ? ""
                      : " (previous known value: " + previousKnownInvocationCounter + ")");
            }
          } catch (final FunctionalException e) {
            LOGGER.warn("Something went wrong while trying to get the invocation counter", e);
          }
        };
    this.connectionFactory.handlePublicClientConnection(
        messageMetadata, device, null, taskForConnectionManager);
  }

  private long getInvocationCounter(final DlmsConnectionManager connectionManager)
      throws FunctionalException {
    final Number invocationCounter =
        this.dlmsHelper
            .getAttributeValue(connectionManager, ATTRIBUTE_ADDRESS_INVOCATION_COUNTER_VALUE)
            .getValue();
    return invocationCounter.longValue();
  }
}
