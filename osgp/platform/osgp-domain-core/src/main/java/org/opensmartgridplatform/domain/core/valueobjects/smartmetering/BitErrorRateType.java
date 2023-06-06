// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

public enum BitErrorRateType {
  RXQUAL_0,
  RXQUAL_1,
  RXQUAL_2,
  RXQUAL_3,
  RXQUAL_4,
  RXQUAL_5,
  RXQUAL_6,
  RXQUAL_7,
  NOT_KNOWN_OR_NOT_DETECTABLE;

  public String value() {
    return this.name();
  }

  public static BitErrorRateType fromValue(final String v) {
    return valueOf(v);
  }
}
