//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dlms.interfaceclass.method;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the methods defined for IC SecuritySetup. */
public enum SecuritySetupMethod implements MethodClass {
  SECURITY_ACTIVATE(1, false),
  GLOBALE_KEY_TRANSFER(2, false);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.SECURITY_SETUP;

  private final int methodId;
  private final boolean mandatory;

  private SecuritySetupMethod(final int methodId, final boolean mandatory) {
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
