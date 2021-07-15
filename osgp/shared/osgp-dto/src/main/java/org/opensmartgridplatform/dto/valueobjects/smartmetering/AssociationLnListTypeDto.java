/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
