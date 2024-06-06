// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC GsmDiagnostic. */
public enum GsmDiagnosticAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  OPERATOR(2),
  MODEM_REGISTRATION_STATUS(3, AttributeType.MODEM_REGISTRATION_STATUS),
  CIRCUIT_SWITCHED_STATUS(4, AttributeType.CIRCUIT_SWITCHED_STATUS),
  PACKET_SWITCHED_STATUS(5, AttributeType.PACKET_SWITCHED_STATUS),
  CELL_INFO(6, AttributeType.CELL_INFO),
  ADJACENT_CELLS(7, AttributeType.ADJACENT_CELLS),
  CAPTURE_TIME(8, AttributeType.DATE_TIME);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.GSM_DIAGNOSTIC;

  private final int attributeId;

  private final AttributeType attributeType;

  private GsmDiagnosticAttribute(final int attributeId) {
    this.attributeId = attributeId;
    this.attributeType = AttributeType.UNKNOWN;
  }

  private GsmDiagnosticAttribute(final int attributeId, final AttributeType attributeType) {
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
