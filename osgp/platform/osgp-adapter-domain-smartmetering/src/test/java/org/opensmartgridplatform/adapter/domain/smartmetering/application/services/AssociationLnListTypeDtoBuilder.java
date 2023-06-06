// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import java.util.ArrayList;
import java.util.List;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AssociationLnListElementDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AssociationLnListTypeDto;

public class AssociationLnListTypeDtoBuilder {

  private List<AssociationLnListElementDto> associationLnListElementDto = new ArrayList<>();

  public AssociationLnListTypeDto build() {
    return new AssociationLnListTypeDto(this.associationLnListElementDto);
  }

  public AssociationLnListTypeDtoBuilder withEmptyLists() {
    this.associationLnListElementDto = new ArrayList<>();
    return this;
  }

  public AssociationLnListTypeDtoBuilder withNonEmptyLists(
      final AssociationLnListElementDto associationLnListElementDto) {
    this.associationLnListElementDto = new ArrayList<>();
    this.associationLnListElementDto.add(associationLnListElementDto);
    return this;
  }
}
