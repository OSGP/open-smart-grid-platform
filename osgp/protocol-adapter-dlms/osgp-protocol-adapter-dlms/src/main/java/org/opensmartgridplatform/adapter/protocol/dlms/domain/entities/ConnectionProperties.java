// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.entities;

import java.time.Duration;

public class ConnectionProperties {
  private final boolean pingDevice;
  private final boolean initializeInvocationCounter;
  private final Duration waitBeforeInitializingInvocationCounter;
  private final Duration waitBeforeCreatingTheConnection;

  public ConnectionProperties(
      final boolean pingDevice,
      final boolean initializeInvocationCounter,
      final Duration waitBeforeInitializingInvocationCounter,
      final Duration waitBeforeCreatingTheConnection) {
    this.pingDevice = pingDevice;
    this.initializeInvocationCounter = initializeInvocationCounter;
    this.waitBeforeInitializingInvocationCounter = waitBeforeInitializingInvocationCounter;
    this.waitBeforeCreatingTheConnection = waitBeforeCreatingTheConnection;
  }

  public boolean isPingDevice() {
    return this.pingDevice;
  }

  public boolean isInitializeInvocationCounter() {
    return this.initializeInvocationCounter;
  }

  public Duration getWaitBeforeInitializingInvocationCounter() {
    return this.waitBeforeInitializingInvocationCounter;
  }

  public Duration getWaitBeforeCreatingTheConnection() {
    return this.waitBeforeCreatingTheConnection;
  }
}
