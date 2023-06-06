// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public enum PeriodTypeDto {
  DAILY,
  MONTHLY,
  // Interval is 15 minutes for E-meters, hourly for G-meters
  INTERVAL;
}
