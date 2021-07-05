/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import java.util.concurrent.atomic.AtomicInteger;

import org.openmuc.jdlms.RawMessageData;
import org.openmuc.jdlms.RawMessageData.MessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

public class InvocationCountingDlmsMessageListener implements DlmsMessageListener {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(InvocationCountingDlmsMessageListener.class);

  private AtomicInteger numberOfSentMessages = new AtomicInteger(0);

  @Override
  public void messageCaptured(final RawMessageData rawMessageData) {

    if (MessageSource.CLIENT == rawMessageData.getMessageSource()) {
      this.numberOfSentMessages.incrementAndGet();
    }
  }

  @Override
  public void setMessageMetadata(final MessageMetadata messageMetadata) {
    LOGGER.debug("InvocationCountingDlmsMessageListener will be counting for {}", messageMetadata);
  }

  @Override
  public void setDescription(final String description) {
    LOGGER.debug("InvocationCountingDlmsMessageListener will be listening for \"{}\"", description);
  }

  public int getNumberOfSentMessages() {
    return this.numberOfSentMessages.get();
  }
}
