//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dlms.interfaceclass.method;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the methods defined for IC Ipv4Setup. */
public enum Ipv4SetupMethod implements MethodClass {
  ADD_MC_IP_ADDRESS(1, true),
  DELETE_MC_IP_ADDRESS(2, false),
  GET_NBOF_MC_IP_ADDRESSES(3, false);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.IP_V4_SETUP;

  private final int methodId;
  private final boolean mandatory;

  private Ipv4SetupMethod(final int methodId, final boolean mandatory) {
    this.methodId = methodId;
    this.mandatory = mandatory;
  }

  @Override
  public boolean isMandatory() {
    return this.mandatory;
  }

  @Override
  public int getMethodId() {
    return this.methodId;
  }

  @Override
  public InterfaceClass getInterfaceClass() {
    return INTERFACE_CLASS;
  }

  @Override
  public String getMethodName() {
    return this.name();
  }
}
