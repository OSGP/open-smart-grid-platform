/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import java.util.Calendar;
import org.opensmartgridplatform.shared.infra.jms.RetryHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RetryHeaderFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(RetryHeaderFactory.class);

  @Value("${schedule.retry.dlms.delay}")
  private int retryDelay;

  @Value("${schedule.retry.dlms.retries}")
  private int maximumRetries;

  @Value("${schedule.retry.dlms.backoff.multiplier}")
  private int backoffMultiplier;

  @Value("${schedule.retry.dlms.backoff.exponential}")
  private boolean exponentialBackoff;

  public RetryHeader createRetryHeader(final int retry) {
    final Calendar retryTime = Calendar.getInstance();
    retryTime.add(Calendar.MILLISECOND, this.calculateDelay(retry));

    LOGGER.info(
        "Creating retry header for retryCount: {}, maximumRetries: {}, scheduledRetryTime: {}.",
        retry,
        this.maximumRetries,
        retryTime.getTime());
    return new RetryHeader(retry, this.maximumRetries, retryTime.getTime());
  }

  public RetryHeader createEmtpyRetryHeader() {
    return new RetryHeader();
  }

  private int calculateDelay(final int retry) {
    int delay = this.retryDelay;
    if (this.exponentialBackoff) {
      final double factor = Math.pow(this.backoffMultiplier, retry);
      delay *= factor;
    }
    return delay;
  }
}
