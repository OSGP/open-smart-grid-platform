/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.shared.health;

public interface HealthCheck {

  /**
   * Returns whether the check outcome is OK or NOT OK.
   *
   * @return HealthResponse
   */
  HealthResponse isHealthy();
}
