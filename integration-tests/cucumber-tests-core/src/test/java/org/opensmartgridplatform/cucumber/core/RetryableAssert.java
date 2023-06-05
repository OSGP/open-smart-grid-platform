// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.core;

import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RetryableAssert {

  private RetryableAssert() {
    // Private constructor, utility class.
  }

  /**
   * Runs the given {@code assertion}, rescheduling it with the given {@code delay} if an {@link
   * AssertionError} occurs, for a maximum of {@code numberOfRetries} times.
   */
  public static void assertWithRetries(
      final Runnable assertion, final int numberOfRetries, final long delay, final TimeUnit unit) {

    try {
      assertion.run();
    } catch (final AssertionError e) {
      if (numberOfRetries < 1) {
        throw e;
      }
      assertDelayedWithRetries(assertion, numberOfRetries - 1, delay, unit);
    }
  }

  /**
   * Runs the given {@code assertion} after an initial {@code delay}, rescheduling it with that same
   * {@code delay} if an {@link AssertionError} occurs, for a maximum of {@code numberOfRetries}
   * times.
   */
  public static void assertDelayedWithRetries(
      final Runnable assertion, final int numberOfRetries, final long delay, final TimeUnit unit) {

    try {
      Executors.newSingleThreadScheduledExecutor()
          .schedule(() -> assertWithRetries(assertion, numberOfRetries, delay, unit), delay, unit)
          .get();
    } catch (final InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new CompletionException(ie);
    } catch (final ExecutionException ee) {
      if (ee.getCause() instanceof AssertionError) {
        throw (AssertionError) ee.getCause();
      } else {
        throw new CompletionException(ee.getCause());
      }
    }
  }

  public static String describeMaxDuration(
      final int numberOfDelays, final long delay, final TimeUnit unit) {
    return Duration.ofMillis(unit.toMillis(numberOfDelays * delay))
        .toString()
        .substring(2)
        .toLowerCase(Locale.UK);
  }
}
