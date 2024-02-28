/*
 * SPDX-FileCopyrightText: Copyright Contributors to the GXF project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensmartgridplatform.throttling;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class NewConnectionRequestThrottlerTest {
  private final int PRIORITY = 8;
  private final int MAX_NEW_CONNECTION_REQUESTS = 1;
  private final long RESET_TIME_IN_MS = 2000;
  private final long MAX_WAIT_FOR_NEW_CONNECTION_REQUEST_IN_MS = 100;

  @Test
  void isNewConnectionRequestAllowed() {
    final NewConnectionRequestThrottler throttler =
        new NewConnectionRequestThrottler(
            this.MAX_NEW_CONNECTION_REQUESTS,
            this.RESET_TIME_IN_MS,
            this.MAX_WAIT_FOR_NEW_CONNECTION_REQUEST_IN_MS);

    long startTime = System.currentTimeMillis();
    // First success
    assertThat(throttler.isNewConnectionRequestAllowed(this.PRIORITY)).isTrue();
    assertThat(System.currentTimeMillis() - startTime)
        .isLessThan(this.MAX_WAIT_FOR_NEW_CONNECTION_REQUEST_IN_MS);

    // Second fail
    startTime = System.currentTimeMillis();
    assertThat(throttler.isNewConnectionRequestAllowed(this.PRIORITY)).isFalse();
    assertThat(System.currentTimeMillis() - startTime)
        .isGreaterThanOrEqualTo(this.MAX_WAIT_FOR_NEW_CONNECTION_REQUEST_IN_MS);
  }

  @Test
  void newConnectionRequestAllowedAfterReset() {
    final NewConnectionRequestThrottler throttler =
        new NewConnectionRequestThrottler(
            this.MAX_NEW_CONNECTION_REQUESTS,
            this.RESET_TIME_IN_MS,
            this.MAX_WAIT_FOR_NEW_CONNECTION_REQUEST_IN_MS);

    final long startTime = System.currentTimeMillis();
    // First success
    assertThat(throttler.isNewConnectionRequestAllowed(this.PRIORITY)).isTrue();
    assertThat(System.currentTimeMillis() - startTime)
        .isLessThan(this.MAX_WAIT_FOR_NEW_CONNECTION_REQUEST_IN_MS);

    // Second fail
    assertThat(throttler.isNewConnectionRequestAllowed(this.PRIORITY)).isFalse();
    assertThat(System.currentTimeMillis() - startTime).isLessThan(this.RESET_TIME_IN_MS);

    // Third success after reset
    await()
        .atMost(this.RESET_TIME_IN_MS + 100, TimeUnit.MILLISECONDS)
        .untilAsserted(
            () -> assertThat(throttler.isNewConnectionRequestAllowed(this.PRIORITY)).isTrue());
  }

  @Test
  void resetManyTimesAndFast() {
    final NewConnectionRequestThrottler throttler = new NewConnectionRequestThrottler(1, 1, 100);

    for (int i = 0; i < 1000; i++) {
      final long startTime = System.currentTimeMillis();

      assertThat(throttler.isNewConnectionRequestAllowed(this.PRIORITY)).isTrue();

      assertThat(System.currentTimeMillis() - startTime)
          .isLessThan(this.MAX_WAIT_FOR_NEW_CONNECTION_REQUEST_IN_MS);
    }
  }
}
