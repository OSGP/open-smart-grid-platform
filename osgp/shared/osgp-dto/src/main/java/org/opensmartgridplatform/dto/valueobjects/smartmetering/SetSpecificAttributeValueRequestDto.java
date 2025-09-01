// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class SetSpecificAttributeValueRequestDto implements ActionRequestDto {

  @Serial private static final long serialVersionUID = 6091630820323702494L;

  private final List<ValueToSetDto> valuesToSet;

  public SetSpecificAttributeValueRequestDto(final List<ValueToSetDto> valuesToSet) {
    this.valuesToSet = new ArrayList<>(valuesToSet);
  }

  public List<ValueToSetDto> getValuesToSet() {
    return new ArrayList<>(this.valuesToSet);
  }
}
