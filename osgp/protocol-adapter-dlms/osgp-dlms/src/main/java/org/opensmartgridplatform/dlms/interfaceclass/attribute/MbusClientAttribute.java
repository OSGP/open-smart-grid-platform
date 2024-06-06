// Copyright 2012-20 Fraunhofer ISE
// Copyright 2020 Alliander N.V.
// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC MbusClient. */
public enum MbusClientAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  MBUS_PORT_REFERENCE(2),
  CAPTURE_DEFINITION(3),
  CAPTURE_PERIOD(4),
  PRIMARY_ADDRESS(5),
  IDENTIFICATION_NUMBER(6),
  MANUFACTURER_ID(7),
  VERSION(8),
  DEVICE_TYPE(9),
  ACCESS_NUMBER(10),
  STATUS(11),
  ALARM(12),
  CONFIGURATION(13),
  ENCRYPTION_KEY_STATUS(14);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.MBUS_CLIENT;

  private final int attributeId;

  private MbusClientAttribute(final int attributeId) {
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
