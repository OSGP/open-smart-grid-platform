//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TestAlarmSchedulerRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = 4501989071785153393L;

  private TestAlarmTypeDto alarmType;
  private Date scheduleTime;
}
