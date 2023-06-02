//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class FaultResponseParameter implements Serializable {

  private static final long serialVersionUID = -551244954351120677L;

  private final String key;
  private final String value;

  public FaultResponseParameter(final String key, final String value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public String toString() {
    return "[" + this.key + " => " + this.value + "]";
  }

  public String getKey() {
    return this.key;
  }

  public String getValue() {
    return this.value;
  }
}
