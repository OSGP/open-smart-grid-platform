/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

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
