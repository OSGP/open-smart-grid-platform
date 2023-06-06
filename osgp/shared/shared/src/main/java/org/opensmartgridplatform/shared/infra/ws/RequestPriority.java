// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
