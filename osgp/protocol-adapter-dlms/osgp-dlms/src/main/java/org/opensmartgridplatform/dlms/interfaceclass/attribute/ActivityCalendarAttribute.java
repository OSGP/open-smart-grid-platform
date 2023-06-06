/*
 * Copyright 2020 Alliander N.V.
 * Copyright 2012-20 Fraunhofer ISE
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * This file was originally part of jDLMS, where it was part of a group of classes residing in
 * packages org.openmuc.jdlms.interfaceclass, org.openmuc.jdlms.interfaceclass.attribute and
 * org.openmuc.jdlms.interfaceclass.method that have been deprecated for jDLMS since version 1.5.1.
 *
 * It has been copied to the GXF code base under the Apache License, Version 2.0 with the
 * permission of Fraunhofer ISE. For more information about jDLMS visit
 *
 * http://www.openmuc.org
 */
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
