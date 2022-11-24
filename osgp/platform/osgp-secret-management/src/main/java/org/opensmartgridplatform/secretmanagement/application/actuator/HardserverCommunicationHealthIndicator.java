/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.actuator;

import org.opensmartgridplatform.secretmanagement.application.services.SecretManagementMetrics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class HardserverCommunicationHealthIndicator implements HealthIndicator {
  private final SecretManagementMetrics secretManagementMetrics;

  private final String message_key = "HardserverCommunicationExceptions";

  private final int hardserverExceptionThreshold;

  public HardserverCommunicationHealthIndicator(
      final SecretManagementMetrics secretManagementMetrics,
      @Value("${hardserver.exception.threshold}") final int hardserverExceptionThreshold) {
    this.secretManagementMetrics = secretManagementMetrics;
    this.hardserverExceptionThreshold = hardserverExceptionThreshold;
  }

  @Override
  public Health health() {
    final int nrOfExceptions =
        this.secretManagementMetrics.countHardserverCommunicationExceptions();
    if (nrOfExceptions >= this.hardserverExceptionThreshold) {
      return Health.down().withDetail(this.message_key, nrOfExceptions).build();
    }
    return Health.up().withDetail(this.message_key, nrOfExceptions).build();
  }
}
