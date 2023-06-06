// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.ws;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;

public class RequestContext {

  public static final String BYPASS_RETRY = "BypassRetry";
  public static final String MESSAGE_PRIORITY = "MessagePriority";
  public static final String MAX_SCHEDULE_TIME = "MaxScheduleTime";
  private final int messagePriority;
  private final boolean bypassRetry;
  private final Long maxScheduleTime;

  private RequestContext(
      final boolean bypassRetryValue, final int messagePriority, final Long maxScheduleTime) {
    this.bypassRetry = bypassRetryValue;
    this.messagePriority = messagePriority;
    this.maxScheduleTime = maxScheduleTime;
  }

  private RequestContext(
      final boolean bypassRetryValue,
      final MessagePriorityEnum messagePriority,
      final Long maxScheduleTime) {
    this.bypassRetry = bypassRetryValue;
    this.messagePriority = messagePriority.getPriority();
    this.maxScheduleTime = maxScheduleTime;
  }

  public Map<String, String> asMap() {

    final Map<String, String> map = new HashMap<>();

    map.put(MESSAGE_PRIORITY, String.valueOf(this.messagePriority));
    map.put(BYPASS_RETRY, String.valueOf(this.bypassRetry));
    if (this.maxScheduleTime != null) {
      map.put(MAX_SCHEDULE_TIME, String.valueOf(this.maxScheduleTime));
    }
    return map;
  }

  public int getMessagePriority() {
    return this.messagePriority;
  }

  public boolean isBypassRetry() {
    return this.bypassRetry;
  }

  public Long getMaxScheduleTime() {
    return this.maxScheduleTime;
  }

  public static class Builder {
    private int messagePriority = RequestPriority.DEFAULT_REQUEST_PRIORITY.getValue();
    private boolean bypassRetry = false;
    private Long maxScheduleTime;

    public Builder bypassRetryValue(final boolean bypassRetry) {
      this.bypassRetry = bypassRetry;
      return this;
    }

    public Builder messagePriority(final int messagePriority) {
      this.messagePriority = messagePriority;
      return this;
    }

    public Builder messagePriority(final MessagePriorityEnum messagePriority) {
      this.messagePriority = messagePriority.getPriority();
      return this;
    }

    public Builder maxScheduleTime(final Optional<Date> maxScheduleTime) {
      if (maxScheduleTime.isPresent()) {
        this.maxScheduleTime = maxScheduleTime.get().getTime();
      }
      return this;
    }

    public RequestContext build() {
      return new RequestContext(this.bypassRetry, this.messagePriority, this.maxScheduleTime);
    }
  }
}
