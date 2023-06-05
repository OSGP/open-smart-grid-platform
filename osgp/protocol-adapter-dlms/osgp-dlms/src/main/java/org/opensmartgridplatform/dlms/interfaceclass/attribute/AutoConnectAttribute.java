// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC AutoConnect. */
public enum AutoConnectAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  MODE(2),
  REPETITIONS(3),
  REPETITION_DELAY(4),
  CALLING_WINDOW(5),
  DESTINATION_LIST(6);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.AUTO_CONNECT;

  private final int attributeId;

  private AutoConnectAttribute(final int attributeId) {
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
