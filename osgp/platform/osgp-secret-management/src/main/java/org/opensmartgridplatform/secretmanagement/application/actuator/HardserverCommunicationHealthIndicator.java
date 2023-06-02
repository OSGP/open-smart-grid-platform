//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.secretmanagement.application.actuator;

import org.opensmartgridplatform.secretmanagement.application.services.SecretManagementMetrics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class HardserverCommunicationHealthIndicator implements HealthIndicator {
  private final SecretManagementMetrics secretManagementMetrics;

  private static final String MESSAGE_KEY = "HardserverCommunicationExceptions";

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
      return Health.down().withDetail(MESSAGE_KEY, nrOfExceptions).build();
    }
    return Health.up().withDetail(MESSAGE_KEY, nrOfExceptions).build();
  }
}
