//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.health;

public interface HealthCheck {

  /**
   * Returns whether the check outcome is OK or NOT OK.
   *
   * @return HealthResponse
   */
  HealthResponse isHealthy();
}
