// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC SapAssignment. */
public enum SapAssignmentAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  SAP_ASSIGNMENT_LIST(2);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.SAP_ASSIGNMENT;

  private final int attributeId;

  private SapAssignmentAttribute(final int attributeId) {
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
