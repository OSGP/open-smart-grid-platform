/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.infra.ws;

import java.util.HashMap;
import java.util.Map;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;

public class RequestContext {

  public static final String BYPASS_RETRY = "BypassRetry";
  public static final String MESSAGE_PRIORITY = "MessagePriority";
  private final int messagePriority;
  private final boolean bypassRetry;

  private RequestContext(final boolean bypassRetryValue, final int messagePriority) {
    this.bypassRetry = bypassRetryValue;
    this.messagePriority = messagePriority;
  }

  private RequestContext(
      final boolean bypassRetryValue, final MessagePriorityEnum messagePriority) {
    this.bypassRetry = bypassRetryValue;
    this.messagePriority = messagePriority.getPriority();
  }

  public Map<String, String> asMap() {

    final Map<String, String> map = new HashMap<>();

    map.put(MESSAGE_PRIORITY, String.valueOf(this.messagePriority));
    map.put(BYPASS_RETRY, String.valueOf(this.bypassRetry));

    return map;
  }

  public int getMessagePriority() {
    return this.messagePriority;
  }

  public boolean isBypassRetry() {
    return this.bypassRetry;
  }

  public static class Builder {
    private int messagePriority = RequestPriority.DEFAULT_REQUEST_PRIORITY.getValue();
    private boolean bypassRetry = false;

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

    public RequestContext build() {
      return new RequestContext(this.bypassRetry, this.messagePriority);
    }
  }
}
