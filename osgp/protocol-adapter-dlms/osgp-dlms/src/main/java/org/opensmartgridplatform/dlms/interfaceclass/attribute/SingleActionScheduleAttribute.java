//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC SingleActionSchedule. */
public enum SingleActionScheduleAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  EXECUTED_SCRIPT(2),
  TYPE(3),
  EXECUTION_TIME(4);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.SINGLE_ACTION_SCHEDULE;

  private final int attributeId;

  private SingleActionScheduleAttribute(final int attributeId) {
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
