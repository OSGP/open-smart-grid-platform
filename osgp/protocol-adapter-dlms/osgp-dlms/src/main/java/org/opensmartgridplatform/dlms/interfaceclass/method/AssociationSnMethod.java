// Copyright 2012-20 Fraunhofer ISE
// Copyright 2020 Alliander N.V.
// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.method;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the methods defined for IC AssociationSn. */
public enum AssociationSnMethod implements MethodClass {
  READ_BY_LOGICALNAME(3, false),
  GET_ATTRIBUTES_METHODS(4, false),
  CHANGE_LLS_SECRET(5, false),
  CHANGE_HLS_SECRET(6, false),
  REPLY_TO_HLS_AUTHENTICATION(8, false);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.ASSOCIATION_SN;

  private final int methodId;
  private final boolean mandatory;

  private AssociationSnMethod(final int methodId, final boolean mandatory) {
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
