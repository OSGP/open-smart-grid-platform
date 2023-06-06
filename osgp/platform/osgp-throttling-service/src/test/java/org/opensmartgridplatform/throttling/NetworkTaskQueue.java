// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
