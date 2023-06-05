//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC RegisterActivation. */
public enum RegisterActivationAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  REGISTER_ASSIGNMENT(2),
  MASK_LIST(3),
  ACTIVE_MASK(4);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.REGISTER_ACTIVATION;

  private final int attributeId;

  private RegisterActivationAttribute(final int attributeId) {
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
