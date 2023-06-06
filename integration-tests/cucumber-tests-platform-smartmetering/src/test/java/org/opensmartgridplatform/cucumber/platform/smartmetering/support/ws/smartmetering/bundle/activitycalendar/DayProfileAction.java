// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.activitycalendar;

public class DayProfileAction {
  private String startTime;
  private int scriptId;

  public DayProfileAction(final String startTime, final int scriptId) {
    this.startTime = startTime;
    this.scriptId = scriptId;
  }

  public String getStartTime() {
    return this.startTime;
  }

  public int getScriptId() {
    return this.scriptId;
  }
}
