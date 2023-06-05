//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC ExtendedRegister. */
public enum ExtendedRegisterAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  VALUE(2),
  SCALER_UNIT(3),
  STATUS(4),
  CAPTURE_TIME(5);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.EXTENDED_REGISTER;

  private final int attributeId;

  private ExtendedRegisterAttribute(final int attributeId) {
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
