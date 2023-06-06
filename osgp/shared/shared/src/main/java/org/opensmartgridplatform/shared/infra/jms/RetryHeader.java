// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.jms;

import java.io.Serializable;
import java.util.Date;

public class RetryHeader implements Serializable {
  private static final long serialVersionUID = 8503630227262375613L;

  private final int retryCount;
  private final int maxRetries;
  private final Date scheduledRetryTime;

  public RetryHeader() {
    this.retryCount = 0;
    this.maxRetries = 0;
    this.scheduledRetryTime = null;
  }

  public RetryHeader(final int retryCount, final int maxRetries, final Date scheduledRetryTime) {
    this.retryCount = retryCount;
    this.maxRetries = maxRetries;
    this.scheduledRetryTime = scheduledRetryTime;
  }

  public RetryHeader(final int retryCount, final int maxRetries, final long scheduledRetryTime) {
    this(retryCount, maxRetries, new Date(scheduledRetryTime));
  }

  public int getRetryCount() {
    return this.retryCount;
  }

  public int getMaxRetries() {
    return this.maxRetries;
  }

  public Date getScheduledRetryTime() {
    return this.scheduledRetryTime;
  }

  public boolean shouldRetry() {
    return this.scheduledRetryTime != null && this.retryCount < this.maxRetries;
  }
}
