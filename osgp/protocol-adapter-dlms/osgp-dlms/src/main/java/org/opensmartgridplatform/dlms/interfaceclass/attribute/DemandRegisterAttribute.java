//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC DemandRegister. */
public enum DemandRegisterAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  CURRENT_AVERAGE_VALUE(2),
  LAST_AVERAGE_VALUE(3),
  SCALER_UNIT(4),
  STATUS(5),
  CAPTURE_TIME(6),
  START_TIME_CURRENT(7),
  PERIOD(8),
  NUMBER_OF_PERIODS(9);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.DEMAND_REGISTER;

  private final int attributeId;

  private DemandRegisterAttribute(final int attributeId) {
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
