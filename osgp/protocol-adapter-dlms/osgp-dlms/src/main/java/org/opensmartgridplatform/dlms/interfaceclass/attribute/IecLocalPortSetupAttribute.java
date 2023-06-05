//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC IecLocalPortSetup. */
public enum IecLocalPortSetupAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  DEFAULT_MODE(2),
  DEFAULT_BAUD(3),
  PROP_BAUD(4),
  RESPONSE_TIME(5),
  DEVICE_ADDR(6),
  PASS_P1(7),
  PASS_P2(8),
  PASS_W5(9);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.IEC_LOCAL_PORT_SETUP;

  private final int attributeId;

  private IecLocalPortSetupAttribute(final int attributeId) {
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
