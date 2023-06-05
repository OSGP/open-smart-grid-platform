// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.health;

import lombok.Getter;

/** Value object explaining the result of a {@link HealthCheck} */
@Getter
public class HealthResponse {
  /** Indicates whether everything is health */
  private final boolean ok;
  /** In case of an unhealthy situation, this message should indicate what's wrong */
  private final String message;

  private HealthResponse(final boolean ok, final String message) {
    this.ok = ok;
    this.message = message;
  }

  /**
   * Creates a "healthy" response.
   *
   * @return Healty response
   */
  public static HealthResponse ok() {
    return new HealthResponse(true, "");
  }

  /**
   * Creates a "not healthy" response.
   *
   * @param message The message explaining what's wrong
   * @return Not healthy response
   */
  public static HealthResponse notOk(final String message) {
    return new HealthResponse(false, message);
  }
}
