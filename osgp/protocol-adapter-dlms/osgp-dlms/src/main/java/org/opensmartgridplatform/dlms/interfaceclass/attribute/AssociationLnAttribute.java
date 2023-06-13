// Copyright 2012-20 Fraunhofer ISE
// Copyright 2020 Alliander N.V.
// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC AssociationLn. */
public enum AssociationLnAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  OBJECT_LIST(2),
  ASSOCIATED_PARTNERS_ID(3),
  APPLICATION_CONTEXT_NAME(4),
  XDLMS_CONTEXT_INFO(5),
  AUTHENTICATION_MECHANISM_NAME(6),
  LLS_SECRET(7),
  ASSOCIATION_STATUS(8),
  SECURITY_SETUP_REFERENCE(9);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.ASSOCIATION_LN;

  private final int attributeId;

  private AssociationLnAttribute(final int attributeId) {
    this.attributeId = attributeId;
  }

  @Override
  public int attributeId() {
    return this.attributeId;
  }

  @Override
  public String attributeName() {
    return this.name();
  }

  @Override
  public InterfaceClass interfaceClass() {
    return INTERFACE_CLASS;
  }
}
