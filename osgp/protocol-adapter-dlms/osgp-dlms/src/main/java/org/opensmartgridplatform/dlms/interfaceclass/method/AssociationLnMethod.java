//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dlms.interfaceclass.method;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the methods defined for IC AssociationLn. */
public enum AssociationLnMethod implements MethodClass {
  REPLY_TO_HLS_AUTHENTICATION(1, false),
  CHANGE_HLS_SECRET(2, false),
  ADD_OBJECT(3, false),
  REMOVE_OBJECT(4, false);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.ASSOCIATION_LN;

  private final int methodId;
  private final boolean mandatory;

  private AssociationLnMethod(final int methodId, final boolean mandatory) {
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
