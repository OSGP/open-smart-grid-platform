// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.joda.time.DateTime;
import org.opensmartgridplatform.domain.core.validation.ScheduleConstraints;

@ScheduleConstraints
public class ScheduleEntry implements Serializable {

  /** Serial version ID. */
  private static final long serialVersionUID = -1935955961954576644L;

  @NotNull private WeekDayType weekDay;

  private DateTime startDay;

  private DateTime endDay;

  @NotNull private ActionTimeType actionTime;

  private String time;

  private WindowType triggerWindow;

  private Integer index;

  private Boolean isEnabled;

  private Integer minimumLightsOn;

  @NotNull
  @Size(min = 1, max = 6, message = "Schedule should contain 1 to 6 lightvalues")
  @Valid
  private List<LightValue> lightValue;

  private TriggerType triggerType;

  public WeekDayType getWeekDay() {
    return this.weekDay;
  }

  public void setWeekDay(final WeekDayType value) {
    this.weekDay = value;
  }

  public void setStartDay(final DateTime value) {
    this.startDay = value;
  }

  public DateTime getStartDay() {
    return this.startDay;
  }

  public void setEndDay(final DateTime value) {
    this.endDay = value;
  }

  public DateTime getEndDay() {
    return this.endDay;
  }

  public void setActionTime(final ActionTimeType value) {
    this.actionTime = value;
  }

  public ActionTimeType getActionTime() {
    return this.actionTime;
  }

  public void setTime(final String value) {
    this.time = value;
  }

  public String getTime() {
    return this.time;
  }

  public void setTriggerWindow(final WindowType value) {
    this.triggerWindow = value;
  }

  public WindowType getTriggerWindow() {
    return this.triggerWindow;
  }

  public void setLightValue(final List<LightValue> value) {
    this.lightValue = value;
  }

  public List<LightValue> getLightValue() {
    return this.lightValue;
  }

  public void setTriggerType(final TriggerType triggerType) {
    this.triggerType = triggerType;
  }

  public TriggerType getTriggerType() {
    return this.triggerType;
  }

  public Integer getIndex() {
    return this.index;
  }

  public void setIndex(final Integer index) {
    this.index = index;
  }

  public Boolean getIsEnabled() {
    return this.isEnabled;
  }

  public void setIsEnabled(final Boolean isEnabled) {
    this.isEnabled = isEnabled;
  }

  public Integer getMinimumLightsOn() {
    return this.minimumLightsOn;
  }

  public void setMinimumLightsOn(final Integer minimumLightsOn) {
    this.minimumLightsOn = minimumLightsOn;
  }
}
