// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;

@Getter
public class ValueToSetDto implements Serializable {

  @Serial private static final long serialVersionUID = -2663395295387396979L;

  private final String objectType;
  private final int attribute;
  private final Integer intValue;

  public ValueToSetDto(final String objectType, final int attribute, final Integer intValue) {
    this.objectType = objectType;
    this.attribute = attribute;
    this.intValue = intValue;
  }
}
