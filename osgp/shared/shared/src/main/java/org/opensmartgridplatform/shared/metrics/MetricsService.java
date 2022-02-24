/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.shared.metrics;

/** Interface to interact with a Metrics service */
public interface MetricsService {

  /**
   * Increase the counter identified by {@code name} by 1.
   *
   * @param name Name of the counter
   */
  void increaseCounter(String name);

  /**
   * Increase the counter identified by {@code name} by 1.
   *
   * @param name Name of the counter
   * @param description Description of the counter for humans
   */
  void increaseCounter(String name, String description);

  /**
   * Measure the time it takes to run {@code runnable} and report it under {@code name}. This is for
   * long running process like batch jobs.
   *
   * @param runnable Code to be measured
   * @param name Name of the timer
   */
  void longTime(Runnable runnable, String name);

  /**
   * Measure the time it takes to run {@code runnable} and report it under {@code name}. This is for
   * long running process like batch jobs.
   *
   * @param runnable Code to be measured
   * @param name Name of the timer
   * @param description Description of the counter for humans
   */
  void longTime(Runnable runnable, String name, String description);
}
