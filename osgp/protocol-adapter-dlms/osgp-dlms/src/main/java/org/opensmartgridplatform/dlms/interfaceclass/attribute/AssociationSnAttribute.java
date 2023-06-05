//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dlms.interfaceclass.attribute;

import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;

/** This class contains the attributes defined for IC AssociationSn. */
public enum AssociationSnAttribute implements AttributeClass {
  LOGICAL_NAME(1),
  OBJECT_LIST(2),
  ACCESS_RIGHTS_LIST(3),
  SECURITY_SETUP_REFERENCE(4);

  static final InterfaceClass INTERFACE_CLASS = InterfaceClass.ASSOCIATION_SN;

  private final int attributeId;

  private AssociationSnAttribute(final int attributeId) {
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
