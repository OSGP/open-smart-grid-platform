// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;

public class LightValueDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 3783788109559927722L;

  private final Integer index;

  private boolean on;

  private final Integer dimValue;

  public LightValueDto(final Integer index, final boolean on, final Integer dimValue) {
    this.index = index;
    this.on = on;
    this.dimValue = dimValue;
  }

  public Integer getIndex() {
    return this.index;
  }

  public boolean isOn() {
    return this.on;
  }

  public Integer getDimValue() {
    return this.dimValue;
  }

  public void invertIsOn() {
    this.on = !this.on;
  }
}
