// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.core;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.StaleElementReferenceException;
import org.opensmartgridplatform.cucumber.core.config.CoreApplicationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Generic class with wait methods. */
@Component
public class Wait {

  /** Platform configuration is needed for timeout settings. */
  private static CoreApplicationConfiguration configuration;

  /**
   * Autowire the configuration.
   *
   * @param configuration The platform configuration.
   */
  @Autowired
  public Wait(final CoreApplicationConfiguration configuration) {
    Wait.configuration = configuration;
  }

  /**
   * Wrap a collection of code and safely execute this in a timeout harness.
   *
   * @param <T> object of specific type
   * @param task bundle of code
   * @return object of a specific type T
   */
  public static <T> T untilAndReturn(final Callable<T> task) {
    final Logger logger = LoggerFactory.getLogger(Wait.class);

    T response = null;
    boolean success = false;
    final Instant startInstant = Instant.now();
    final Instant timeoutInstant = startInstant.plusSeconds(configuration.getTimeout());
    while (!success) {
      if (Instant.now().isAfter(timeoutInstant)) {
        Assertions.fail("Timeout after [" + configuration.getTimeout() + "] seconds.");
      }

      try {
        // Call the code to run
        response = task.call();

        // If we get here set success to true, so make sure that we
        // stop.
        success = true;
        // We have a response, so exit
        // the while loop immediately
        break;
      } catch (final StaleElementReferenceException domError) {
        handleStaleELementError(logger, domError);
      } catch (final AssertionError error) {
        handleAssertionError(logger, error);
      } catch (final Exception ex) {
        handleException(logger, ex);
      }

      logger.info(
          "... polling in Wait.until ("
              + Duration.between(startInstant, Instant.now()).getSeconds()
              + " seconds) ...");
      try {
        TimeUnit.MILLISECONDS.sleep(configuration.getSleepTime());
      } catch (final Exception ex) {
        handleException(logger, ex);
      }
    }
    return response;
  }

  public static void until(final Runnable task) {
    until(task, configuration.getTimeout());
  }

  public static void until(final Runnable task, final int timeoutSeconds) {
    until(task, timeoutSeconds, configuration.getSleepTime());
  }

  public static void until(
      final Runnable task, final int timeoutSeconds, final int sleeptimeMillis) {

    final Logger logger = LoggerFactory.getLogger(Wait.class);

    boolean success = false;
    final Instant startInstant = Instant.now();
    final Instant timeoutInstant = startInstant.plusSeconds(timeoutSeconds);
    while (!success) {
      if (Instant.now().isAfter(timeoutInstant)) {
        Assertions.fail("Timeout after [" + timeoutSeconds + "] seconds.");
      }

      try {
        // Call the code to run
        task.run();

        // If we get here set success to true, so make sure that we
        // stop.
        success = true;
        // We have a response, so exit
        // the while loop immediately
        break;
      } catch (final StaleElementReferenceException domError) {
        handleStaleELementError(logger, domError);
      } catch (final AssertionError error) {
        handleAssertionError(logger, error);
      } catch (final Exception ex) {
        handleException(logger, ex);
      }

      logger.info(
          "... polling in Wait.until ("
              + Duration.between(startInstant, Instant.now()).getSeconds()
              + " seconds) ...");
      try {
        TimeUnit.MILLISECONDS.sleep(sleeptimeMillis);
      } catch (final Exception ex) {
        handleException(logger, ex);
      }
    }
  }

  private static void handleStaleELementError(
      final Logger logger, final StaleElementReferenceException ex) {
    logger.error("Stale element error: " + ex.getMessage());
    logger.debug("               : stacktrace [" + Arrays.toString(ex.getStackTrace()) + "]");
  }

  private static void handleAssertionError(final Logger logger, final AssertionError error) {
    logger.error("Assertion error: " + error.getMessage());
    logger.debug("               : stacktrace [" + Arrays.toString(error.getStackTrace()) + "]");
  }

  /** Logs the exception. */
  private static void handleException(final Logger logger, final Exception ex) {
    logger.error("Caught an exception: [{}]", ex.getMessage());
    logger.debug("                   : stacktrace [" + Arrays.toString(ex.getStackTrace()) + "]");
  }
}
