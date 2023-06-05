// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC GsmDiagnostic. */
public enum GsmDiagnosticAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  OPERATOR(2),
  MODEM_REGISTRATION_STATUS(3),
  CIRCUIT_SWITCHED_STATUS(4),
  PACKET_SWITCHED_STATUS(5),
  CELL_INFO(6),
  ADJACENT_CELLS(7),
  CAPTURE_TIME(8);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.GSM_DIAGNOSTIC;

  private final int attributeId;

  private GsmDiagnosticAttribute(final int attributeId) {
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
