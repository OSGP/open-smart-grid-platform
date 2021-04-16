/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AssociationLnListType implements Serializable {
  private static final long serialVersionUID = 5540577793697751858L;

  private final List<AssociationLnListElement> associationLnListElement;

  public AssociationLnListType(final List<AssociationLnListElement> associationLnListElement) {
    this.associationLnListElement = Collections.unmodifiableList(associationLnListElement);
  }

  public List<AssociationLnListElement> getAssociationLnListElement() {
    return new ArrayList<>(this.associationLnListElement);
  }
}
