/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */
package org.opensmartgridplatform.adapter.protocol.dlms.exceptions;

import java.time.Duration;

/*
 * If this exception is thrown during processing of a request message
 * the DeviceRequestMessageProcessor will put the request back on the queue with a delay.
 * Hereby the request will NOT be sent back to the Core layer
 * to retry the request using the RetryHeader.
 */
public class DirectlyRetryableException extends RuntimeException {

  private static final long serialVersionUID = -4887139219288851998L;

  private Duration delay;

  public DirectlyRetryableException(final Duration delay) {
    this.delay = delay;
  }

  public Duration getDelay() {
    return this.delay;
  };
}
