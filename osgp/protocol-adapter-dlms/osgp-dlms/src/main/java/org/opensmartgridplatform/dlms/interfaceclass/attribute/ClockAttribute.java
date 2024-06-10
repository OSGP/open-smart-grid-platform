// Copyright 2012-20 Fraunhofer ISE
// Copyright 2020 Alliander N.V.
// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.CLOCK_STATUS;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.DATE_TIME;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.UNKNOWN;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC Clock. */
public enum ClockAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  TIME(2, DATE_TIME),
  TIME_ZONE(3),
  STATUS(4, CLOCK_STATUS),
  DAYLIGHT_SAVINGS_BEGIN(5, DATE_TIME),
  DAYLIGHT_SAVINGS_END(6, DATE_TIME),
  DAYLIGHT_SAVINGS_DEVIATION(7),
  DAYLIGHT_SAVINGS_ENABLED(8),
  CLOCK_BASE(9, AttributeType.CLOCK_BASE);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.CLOCK;

  private final int attributeId;

  private final AttributeType attributeType;

  ClockAttribute(final int attributeId) {
    this.attributeId = attributeId;
    this.attributeType = UNKNOWN;
  }

  ClockAttribute(final int attributeId, final AttributeType attributeType) {
    this.attributeId = attributeId;
    this.attributeType = attributeType;
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

  @Override
  public AttributeType attributeType() {
    return this.attributeType;
  }
}
