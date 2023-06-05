// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC GprsModemSetup. */
public enum GprsModemSetupAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  APN(2),
  PIN_CODE(3),
  QUALITY_OF_SERVICE(4);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.GPRS_MODEM_SETUP;

  private final int attributeId;

  private GprsModemSetupAttribute(final int attributeId) {
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
