// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC ActivityCalendar. */
public enum ActivityCalendarAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  CALENDAR_NAME_ACTIVE(2),
  SEASON_PROFILE_ACTIVE(3),
  WEEK_PROFILE_TABLE_ACTIVE(4),
  DAY_PROFILE_TABLE_ACTIVE(5),
  CALENDAR_NAME_PASSIVE(6),
  SEASON_PROFILE_PASSIVE(7),
  WEEK_PROFILE_TABLE_PASSIVE(8),
  DAY_PROFILE_TABLE_PASSIVE(9),
  ACTIVATE_PASSIVE_CALENDAR_TIME(10);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.ACTIVITY_CALENDAR;

  private final int attributeId;

  private ActivityCalendarAttribute(final int attributeId) {
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
