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

public enum RequestPriority {
  DEFAULT_REQUEST_PRIORITY(4);

  private int priority;

  private RequestPriority(final int priority) {
    this.priority = priority;
  }

  public int getValue() {
    return this.priority;
  }
}
