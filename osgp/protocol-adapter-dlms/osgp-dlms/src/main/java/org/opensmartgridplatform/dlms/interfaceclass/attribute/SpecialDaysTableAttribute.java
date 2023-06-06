// Copyright 2012-20 Fraunhofer ISE
// Copyright 2020 Alliander N.V.
// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC SpecialDaysTable. */
public enum SpecialDaysTableAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  ENTRIES(2);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.SPECIAL_DAYS_TABLE;

  private final int attributeId;

  private SpecialDaysTableAttribute(final int attributeId) {
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
