// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.da;

import java.io.Serializable;

public class GetHealthStatusResponseDto implements Serializable {
  private static final long serialVersionUID = 4776483459295816646L;

  private final String healthStatus;

  public GetHealthStatusResponseDto(final String healthStatus) {
    this.healthStatus = healthStatus;
  }

  public String getHealthStatus() {
    return this.healthStatus;
  }
}
