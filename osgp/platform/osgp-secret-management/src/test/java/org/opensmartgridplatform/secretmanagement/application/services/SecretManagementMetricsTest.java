/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;

@ExtendWith(MockitoExtension.class)
class SecretManagementMetricsTest {

  @Mock private MeterRegistry meterRegistry;
  private SecretManagementMetrics secretManagementMetrics;

  @BeforeEach
  public void setUpSecretManagementService() {
    this.secretManagementMetrics =
        new SecretManagementMetrics(
            this.meterRegistry, MyHardserverConnectionError.class.getName());
  }

  @Test
  void incrementEncrypterException() {
    final Counter counter = mock(Counter.class);
    when(this.meterRegistry.counter("encrypter.exception")).thenReturn(counter);

    this.secretManagementMetrics.incrementEncrypterException(new EncrypterException("1"));

    verify(counter).increment();
    verify(this.meterRegistry, never()).counter("hardserver.communication.exception");
  }

  @Test
  void incrementHardserverConnectionException() {
    final Counter counter = mock(Counter.class);
    when(this.meterRegistry.counter("encrypter.exception")).thenReturn(counter);
    final Counter hardsererCommunicationCounter = mock(Counter.class);
    when(this.meterRegistry.counter("hardserver.communication.exception"))
        .thenReturn(hardsererCommunicationCounter);

    this.secretManagementMetrics.incrementEncrypterException(
        new EncrypterException("1", new MyHardserverConnectionError()));

    verify(counter).increment();
    verify(hardsererCommunicationCounter).increment();
  }

  @Test
  void incrementOtherException() {
    final Counter counter = mock(Counter.class);
    when(this.meterRegistry.counter("encrypter.exception")).thenReturn(counter);

    this.secretManagementMetrics.incrementEncrypterException(
        new EncrypterException("1", new Exception()));

    verify(counter).increment();
    verify(this.meterRegistry, never()).counter("hardserver.communication.exception");
  }

  class MyHardserverConnectionError extends Exception {}
}
