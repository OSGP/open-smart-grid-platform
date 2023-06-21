// Copyright 2012-20 Fraunhofer ISE
// Copyright 2020 Alliander N.V.
// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC TcpUdpSetup. */
public enum TcpUdpSetupAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  TCP_UDP_PORT(2),
  IP_REFERENCE(3),
  MSS(4),
  NB_OF_SIM_CONN(5),
  INACTIVITY_TIME_OUT(6);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.TCP_UDP_SETUP;

  private final int attributeId;

  private TcpUdpSetupAttribute(final int attributeId) {
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
