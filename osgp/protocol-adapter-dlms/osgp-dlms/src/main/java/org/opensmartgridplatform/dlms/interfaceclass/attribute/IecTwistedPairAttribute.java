// Copyright 2012-20 Fraunhofer ISE
// Copyright 2020 Alliander N.V.
// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC IecTwistedPair. */
public enum IecTwistedPairAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  SECONDARY_ADDRESS(2),
  PRIMARY_ADDRESS_LIST(3),
  TABI_LIST(4),
  FATAL_ERROR(5);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.IEC_TWISTED_PAIR;

  private final int attributeId;

  private IecTwistedPairAttribute(final int attributeId) {
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
