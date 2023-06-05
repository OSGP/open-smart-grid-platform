// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

public class AssociationLnObjectsResponseData extends ActionResponse {

  private static final long serialVersionUID = 8929591614021002073L;

  private final AssociationLnListType associationLnList;

  public AssociationLnObjectsResponseData(final AssociationLnListType associationLnList) {
    this.associationLnList = associationLnList;
  }

  public AssociationLnListType getAssociationLnList() {
    return this.associationLnList;
  }
}
