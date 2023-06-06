// Copyright 2012-20 Fraunhofer ISE
// Copyright 2020 Alliander N.V.
// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC EthernetSetup. */
public enum EthernetSetupAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  DL_REFERENCE(2),
  IP_ADDRESS_DATA_TYPE(3),
  MULTICAST_IP_ADDRESS(4),
  IP_OPTIONS(5),
  SUBNET_MASK(6),
  GATEWAY_IP_ADDRESS(7),
  USE_DHCP_FLAG(8),
  PRIMARY_DNS_ADDRESS(9),
  SECONDARY_DNS_ADDRESS(10);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.IP_V4_SETUP;

  private final int attributeId;

  private EthernetSetupAttribute(final int attributeId) {
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
