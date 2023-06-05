// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC IecHdlcSetupClass. */
public enum IecHdlcSetupClassAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  COMM_SPEED(2),
  WINDOW_SIZE_TRANSMIT(3),
  WINDOW_SIZE_RECEIVE(4),
  MAX_INFO_FIELD_LENGTH_TRANSMIT(5),
  MAX_INFO_FIELD_LENGTH_RECEIVE(6),
  INTER_OCTET_TIME_OUT(7),
  INACTIVITY_TIME_OUT(8),
  DEVICE_ADDRESS(9);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.IEC_HDLC_SETUP_CLASS;

  private final int attributeId;

  private IecHdlcSetupClassAttribute(final int attributeId) {
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
