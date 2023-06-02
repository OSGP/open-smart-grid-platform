//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class FaultResponseParameterDto implements Serializable {

  private static final long serialVersionUID = 8823187423381882368L;

  private final String key;
  private final String value;

  public FaultResponseParameterDto(final String key, final String value) {
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
