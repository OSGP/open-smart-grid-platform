/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
