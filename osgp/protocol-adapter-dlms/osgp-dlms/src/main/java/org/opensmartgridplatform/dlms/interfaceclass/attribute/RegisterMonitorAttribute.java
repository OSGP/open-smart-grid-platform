//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC RegisterMonitor. */
public enum RegisterMonitorAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  THRESHOLDS(2),
  MONITORED_VALUE(3),
  ACTIONS(4);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.REGISTER_MONITOR;

  private final int attributeId;

  private RegisterMonitorAttribute(final int attributeId) {
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
