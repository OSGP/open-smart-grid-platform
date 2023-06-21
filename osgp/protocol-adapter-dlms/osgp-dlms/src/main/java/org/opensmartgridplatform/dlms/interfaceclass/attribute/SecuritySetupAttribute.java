// Copyright 2012-20 Fraunhofer ISE
// Copyright 2020 Alliander N.V.
// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC SecuritySetup. */
public enum SecuritySetupAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  SECURITY_POLICY(2),
  SECURITY_SUITE(3),
  CLIENT_SYSTEM_TITLE(4),
  SERVER_SYSTEM_TITLE(5);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.SECURITY_SETUP;

  private final int attributeId;

  private SecuritySetupAttribute(final int attributeId) {
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
