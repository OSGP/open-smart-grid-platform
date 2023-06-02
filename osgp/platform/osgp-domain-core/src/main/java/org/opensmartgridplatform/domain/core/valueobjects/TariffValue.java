//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;

public class TariffValue implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -6407884489142251513L;

  protected Integer index;

  protected boolean high;

  /**
   * Gets the value of the index property.
   *
   * @return possible object is {@link Integer }
   */
  public Integer getIndex() {
    return this.index;
  }

  /**
   * Sets the value of the index property.
   *
   * @param value allowed object is {@link Integer }
   */
  public void setIndex(final Integer value) {
    this.index = value;
  }

  /** Gets the value of the high property. */
  public boolean isHigh() {
    return this.high;
  }

  /** Sets the value of the high property. */
  public void setHigh(final boolean value) {
    this.high = value;
  }
}
