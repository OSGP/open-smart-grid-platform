// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public enum EventLogCategory implements Serializable {
  STANDARD_EVENT_LOG,
  FRAUD_DETECTION_LOG,
  COMMUNICATION_SESSION_LOG,
  M_BUS_EVENT_LOG,
  POWER_QUALITY_EVENT_LOG,
  AUXILIARY_EVENT_LOG,
  POWER_QUALITY_EXTENDED_EVENT_LOG,
  POWER_QUALITY_THD_EVENT_LOG;

  public static EventLogCategory fromValue(final String v) {
    return valueOf(v);
  }
}
