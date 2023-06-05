//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC ModemConfiguration. */
public enum ModemConfigurationAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  COMM_SPEED(2),
  INITIALIZATION_STRING(3),
  MODEM_PROFILE(4);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.MODEM_CONFIGURATION;

  private final int attributeId;

  private ModemConfigurationAttribute(final int attributeId) {
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
