// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
