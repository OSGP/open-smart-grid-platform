//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;

public class PsldDataDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 5384893430194329868L;

  private int totalLightingHours;

  public PsldDataDto(final int totalLightingHours) {
    this.totalLightingHours = totalLightingHours;
  }

  public int getTotalLightingHours() {
    return this.totalLightingHours;
  }
}
