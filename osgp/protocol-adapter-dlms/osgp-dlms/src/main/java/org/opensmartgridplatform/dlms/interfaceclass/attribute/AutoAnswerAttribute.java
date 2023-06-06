// Copyright 2012-20 Fraunhofer ISE
// Copyright 2020 Alliander N.V.
// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC AutoAnswer. */
public enum AutoAnswerAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  MODE(2),
  LISTENING_WINDOW(3),
  STATUS(4),
  NUMBER_OF_CALLS(5),
  NUMBER_OF_RINGS(6);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.AUTO_ANSWER;

  private final int attributeId;

  private AutoAnswerAttribute(final int attributeId) {
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
