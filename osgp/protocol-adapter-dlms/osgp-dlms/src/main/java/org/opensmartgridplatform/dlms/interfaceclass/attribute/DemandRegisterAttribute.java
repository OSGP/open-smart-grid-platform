/**
 * Copyright 2020 Alliander N.V. Copyright 2012-20 Fraunhofer ISE
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>This file was originally part of jDLMS, where it was part of a group of classes residing in
 * packages org.openmuc.jdlms.interfaceclass, org.openmuc.jdlms.interfaceclass.attribute and
 * org.openmuc.jdlms.interfaceclass.method that have been deprecated for jDLMS since version 1.5.1.
 *
 * <p>It has been copied to the GXF code base under the Apache License, Version 2.0 with the
 * permission of Fraunhofer ISE. For more information about jDLMS visit
 *
 * <p>http://www.openmuc.org
 */
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
