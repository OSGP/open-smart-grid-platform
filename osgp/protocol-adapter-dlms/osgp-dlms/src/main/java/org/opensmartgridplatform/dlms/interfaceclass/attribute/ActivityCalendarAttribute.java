// Copyright 2012-20 Fraunhofer ISE
// Copyright 2020 Alliander N.V.
// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC ActivityCalendar. */
public enum ActivityCalendarAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  CALENDAR_NAME_ACTIVE(2),
  SEASON_PROFILE_ACTIVE(3, AttributeType.SEASON_PROFILE),
  WEEK_PROFILE_TABLE_ACTIVE(4, AttributeType.WEEK_PROFILE),
  DAY_PROFILE_TABLE_ACTIVE(5, AttributeType.DAY_PROFILE),
  CALENDAR_NAME_PASSIVE(6),
  SEASON_PROFILE_PASSIVE(7, AttributeType.SEASON_PROFILE),
  WEEK_PROFILE_TABLE_PASSIVE(8, AttributeType.WEEK_PROFILE),
  DAY_PROFILE_TABLE_PASSIVE(9, AttributeType.DAY_PROFILE),
  ACTIVATE_PASSIVE_CALENDAR_TIME(10);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.ACTIVITY_CALENDAR;

  private final int attributeId;

  private final AttributeType attributeType;

  private ActivityCalendarAttribute(final int attributeId) {
    this.attributeId = attributeId;
    this.attributeType = AttributeType.UNKNOWN;
  }

  private ActivityCalendarAttribute(final int attributeId, final AttributeType attributeType) {
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
