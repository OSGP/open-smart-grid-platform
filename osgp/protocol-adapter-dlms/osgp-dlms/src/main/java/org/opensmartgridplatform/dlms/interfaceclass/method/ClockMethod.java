//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dlms.interfaceclass.method;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the methods defined for IC Clock. */
public enum ClockMethod implements MethodClass {
  ADJUST_TO_QUARTER(1, false),
  ADJUST_TO_MEASURING_PERIOD(2, false),
  ADJUST_TO_MINUTE(3, false),
  ADJUST_TO_PRESET_TIME(4, false),
  PRESET_ADJUSTING_TIME(5, false),
  SHIFT_TIME(6, false);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.CLOCK;

  private final int methodId;
  private final boolean mandatory;

  private ClockMethod(final int methodId, final boolean mandatory) {
    this.methodId = methodId;
    this.mandatory = mandatory;
  }

  @Override
  public boolean isMandatory() {
    return this.mandatory;
  }

  @Override
  public int getMethodId() {
    return this.methodId;
  }

  @Override
  public InterfaceClass getInterfaceClass() {
    return INTERFACE_CLASS;
  }

  @Override
  public String getMethodName() {
    return this.name();
  }
}
