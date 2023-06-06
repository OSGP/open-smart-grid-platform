// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.da.valueobjects;

import java.io.Serializable;

public class GetHealthStatusResponse implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -6231137161758023435L;

  private final String healthStatus;

  public GetHealthStatusResponse(final String healthStatus) {
    this.healthStatus = healthStatus;
  }

  public String getHealthStatus() {
    return this.healthStatus;
  }
}
