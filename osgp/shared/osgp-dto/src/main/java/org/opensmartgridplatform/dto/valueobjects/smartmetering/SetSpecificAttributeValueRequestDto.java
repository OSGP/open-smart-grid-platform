// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serial;
import lombok.Getter;

@Getter
public class SetSpecificAttributeValueRequestDto implements ActionRequestDto {

  @Serial private static final long serialVersionUID = 6091630820323702494L;

  private final String objectType;
  private final int attribute;
  private final Integer intValue;

  public SetSpecificAttributeValueRequestDto(
      final String objectType, final int attribute, final Integer intValue) {
    super();
    this.objectType = objectType;
    this.attribute = attribute;
    this.intValue = intValue;
  }
}
