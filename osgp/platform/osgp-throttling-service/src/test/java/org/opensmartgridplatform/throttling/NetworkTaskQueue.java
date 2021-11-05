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

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NetworkTaskQueue {

  private final Queue<NetworkTask> queue = new ConcurrentLinkedQueue<>();

  public void add(final NetworkTask networkTask) {
    this.queue.add(networkTask);
  }

  public Optional<NetworkTask> poll() {
    return Optional.ofNullable(this.queue.poll());
  }

  public int remainingNetworkTasks() {
    return this.queue.size();
  }
}
