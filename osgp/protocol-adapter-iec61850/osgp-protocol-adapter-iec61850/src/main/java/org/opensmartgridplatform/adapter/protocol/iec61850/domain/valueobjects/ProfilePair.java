//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects;

import java.util.Date;

public class ProfilePair {

  private final Float[] values;
  private final Date[] times;

  public ProfilePair(final Float[] values, final Date[] times) {
    this.values = values;
    this.times = times;
  }

  public Float[] getValues() {
    return this.values;
  }

  public Date[] getTimes() {
    return this.times;
  }
}
