// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.da.measurements;

public enum ReasonType {
  PERIODIC(1),
  BACKGROUND_SCAN(2),
  SPONTANEOUS(3),
  INTERROGATED_BY_STATION(4);

  private int reasonCode;

  private ReasonType(final int reasonCode) {
    this.reasonCode = reasonCode;
  }

  public int getReasonCode() {
    return this.reasonCode;
  }
}
