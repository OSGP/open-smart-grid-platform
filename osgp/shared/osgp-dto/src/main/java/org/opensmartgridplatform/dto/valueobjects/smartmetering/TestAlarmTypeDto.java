//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;
/*
 * TestAlarmType's for scheduling the AlarmType
 */
public enum TestAlarmTypeDto {
  PARTIAL_POWER_OUTAGE, // will deliver PHASE_OUTAGE_TEST_INDICATION
  LAST_GASP // will deliver LAST_GASP_TEST
}
