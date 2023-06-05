//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC PppSetup. */
public enum PppSetupAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  PHY_REFERENCE(2),
  LCP_OPTIONS(3),
  IPCP_OPTIONS(4),
  PPP_AUTHENTICATION(5);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.PPP_SETUP;

  private final int attributeId;

  private PppSetupAttribute(final int attributeId) {
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
