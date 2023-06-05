// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC Clock. */
public enum ClockAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  TIME(2),
  TIME_ZONE(3),
  STATUS(4),
  DAYLIGHT_SAVINGS_BEGIN(5),
  DAYLIGHT_SAVINGS_END(6),
  DAYLIGHT_SAVINGS_DEVIATION(7),
  DAYLIGHT_SAVINGS_ENABLED(8),
  CLOCK_BASE(9);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.CLOCK;

  private final int attributeId;

  private ClockAttribute(final int attributeId) {
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
