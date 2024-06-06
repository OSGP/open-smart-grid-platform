// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;
import org.hibernate.validator.constraints.Range;
import org.opensmartgridplatform.domain.core.validation.LightValueConstraints;

@LightValueConstraints
public class LightValue implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -2244245336355424779L;

  private static final int MIN_INDEX = 0;
  private static final int MAX_INDEX = 6;

  private static final int MIN_DIMVALUE = 1;
  private static final int MAX_DIMVALUE = 100;

  @Range(min = MIN_INDEX, max = MAX_INDEX)
  private final Integer index;

  private boolean on;

  @Range(min = MIN_DIMVALUE, max = MAX_DIMVALUE)
  private final Integer dimValue;

  public LightValue(final Integer index, final boolean on, final Integer dimValue) {
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
