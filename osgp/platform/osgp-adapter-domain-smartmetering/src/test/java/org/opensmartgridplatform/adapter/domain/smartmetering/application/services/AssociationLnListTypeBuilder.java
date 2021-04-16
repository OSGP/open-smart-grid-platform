/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import java.util.ArrayList;
import java.util.List;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AssociationLnListElement;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AssociationLnListType;

public class AssociationLnListTypeBuilder {

  private List<AssociationLnListElement> associationLnListElement = new ArrayList<>();

  public AssociationLnListType build() {
    return new AssociationLnListType(this.associationLnListElement);
  }

  public AssociationLnListTypeBuilder withEmptyLists() {
    this.associationLnListElement = new ArrayList<>();
    return this;
  }

  public AssociationLnListTypeBuilder withNonEmptyLists(
      final AssociationLnListElement associationLnListElement) {
    this.associationLnListElement = new ArrayList<>();
    this.associationLnListElement.add(associationLnListElement);
    return this;
  }
}
