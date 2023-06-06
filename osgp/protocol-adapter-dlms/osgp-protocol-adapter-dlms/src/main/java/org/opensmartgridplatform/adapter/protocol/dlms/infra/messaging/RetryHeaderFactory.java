// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
  private double backoffMultiplier;

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

  public RetryHeader createEmptyRetryHeader() {
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
