/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.throttling;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class NetworkTask {

  private Random random = new SecureRandom();

  public final int baseTransceiverStationId;
  public final int cellId;
  public final int maxDurationInMillis;

  public volatile boolean finished = false;
  public volatile Throwable throwable = null;

  public NetworkTask(
      final int baseTransceiverStationId, final int cellId, final int maxDurationInMillis) {

    this.baseTransceiverStationId = baseTransceiverStationId;
    this.cellId = cellId;
    this.maxDurationInMillis = maxDurationInMillis;
  }

  public void execute() {
    if (this.finished) {
      throw new IllegalStateException("Task being executed was already handled");
    }
    try {
      TimeUnit.MILLISECONDS.sleep(1L + this.random.nextInt(this.maxDurationInMillis));
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    this.finished = true;
  }
}
