// Copyright 2012-20 Fraunhofer ISE
// Copyright 2020 Alliander N.V.
// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC MbusDiagnostic. */
public enum MbusDiagnosticAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  RECEIVED_SIGNAL_STRENGTH(2),
  CHANNEL_ID(3),
  LINK_STATUS(4),
  BROADCAST_FRAMES_COUNT(5),
  TRANSMISSIONS_COUNTER(6),
  FCS_OK_FRAMES_COUNTER(7),
  FCS_NOK_FRAMES_COUNTER(8),
  CAPTURE_TIME(9);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.MBUS_DIAGNOSTIC;

  private final int attributeId;

  private MbusDiagnosticAttribute(final int attributeId) {
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
