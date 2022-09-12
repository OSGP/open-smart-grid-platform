/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;
/*
 * TestAlarmType's for scheduling the AlarmType
 */
public enum TestAlarmTypeDto {
  PARTIAL_POWER_OUTAGE, // will deliver PHASE_OUTAGE_TEST_INDICATION
  LAST_GASP // will deliver LAST_GASP_TEST_INDICATION
}
