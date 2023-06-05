// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.secretmanagement.application.actuator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.secretmanagement.application.services.SecretManagementMetrics;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

@ExtendWith(MockitoExtension.class)
class HardserverCommunicationHealthIndicatorTest {
  @Mock private SecretManagementMetrics secretManagementMetrics;
  private HardserverCommunicationHealthIndicator healthIndicator;
  private final int hardserverExceptionThreshold = 10;

  @BeforeEach
  void setUp() {
    this.healthIndicator =
        new HardserverCommunicationHealthIndicator(
            this.secretManagementMetrics, this.hardserverExceptionThreshold);
  }

  @Test
  void testUp() {
    when(this.secretManagementMetrics.countHardserverCommunicationExceptions())
        .thenReturn(this.hardserverExceptionThreshold - 1);

    final Health health = this.healthIndicator.health();
    assertThat(health.getStatus()).isEqualTo(Status.UP);
  }

  @Test
  void testDown() {
    when(this.secretManagementMetrics.countHardserverCommunicationExceptions())
        .thenReturn(this.hardserverExceptionThreshold);

    final Health health = this.healthIndicator.health();
    assertThat(health.getStatus()).isEqualTo(Status.DOWN);
  }
}
