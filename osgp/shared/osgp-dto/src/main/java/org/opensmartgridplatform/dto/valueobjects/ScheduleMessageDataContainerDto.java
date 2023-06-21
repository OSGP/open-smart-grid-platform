// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;

public class ScheduleMessageDataContainerDto implements Serializable {

  private static final long serialVersionUID = 3814430143612585535L;

  private ScheduleDto schedule;
  private ConfigurationDto configuration;

  private ScheduleMessageTypeDto scheduleMessageType;

  private PageInfoDto pageInfo;

  private ScheduleMessageDataContainerDto(final Builder builder) {
    this.schedule = builder.schedule;
    this.configuration = builder.configuration;
    this.scheduleMessageType = builder.scheduleMessageType;
    this.pageInfo = builder.pageInfo;
  }

  public ScheduleDto getSchedule() {
    return this.schedule;
  }

  public ConfigurationDto getConfiguration() {
    return this.configuration;
  }

  public ScheduleMessageTypeDto getScheduleMessageType() {
    return this.scheduleMessageType;
  }

  public PageInfoDto getPageInfo() {
    return this.pageInfo;
  }

  public static class Builder {
    // Required parameter
    private ScheduleDto schedule;

    // Optional parameters with default values
    private ConfigurationDto configuration = null;
    private ScheduleMessageTypeDto scheduleMessageType = ScheduleMessageTypeDto.SET_SCHEDULE;
    private PageInfoDto pageInfo = null;

    public Builder(final ScheduleDto schedule) {
      this.schedule = schedule;
    }

    public Builder withConfiguration(final ConfigurationDto configuration) {
      this.configuration = configuration;
      return this;
    }

    public Builder withScheduleMessageType(final ScheduleMessageTypeDto scheduleMessageType) {
      this.scheduleMessageType = scheduleMessageType;
      return this;
    }

    public Builder withPageInfo(final PageInfoDto pageInfo) {
      this.pageInfo = pageInfo;
      return this;
    }

    public ScheduleMessageDataContainerDto build() {
      return new ScheduleMessageDataContainerDto(this);
    }
  }
}
