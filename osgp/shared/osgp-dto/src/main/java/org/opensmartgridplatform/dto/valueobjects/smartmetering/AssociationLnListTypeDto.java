// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AssociationLnListTypeDto extends ActionResponseDto {
  private static final long serialVersionUID = 5540577793697751858L;

  private List<AssociationLnListElementDto> associationLnListElement;

  public AssociationLnListTypeDto(
      final List<AssociationLnListElementDto> associationLnListElement) {
    this.associationLnListElement = Collections.unmodifiableList(associationLnListElement);
  }

  public List<AssociationLnListElementDto> getAssociationLnListElement() {
    return new ArrayList<>(this.associationLnListElement);
  }
}
