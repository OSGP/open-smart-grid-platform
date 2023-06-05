// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class AccessRight implements Serializable {
  private static final long serialVersionUID = -2893677338806870412L;

  private final AttributeAccessDescriptor attributeAccess;
  private final MethodAccessDescriptor methodAccess;

  public AccessRight(
      final AttributeAccessDescriptor attributeAccess, final MethodAccessDescriptor methodAccess) {
    this.attributeAccess = attributeAccess;
    this.methodAccess = methodAccess;
  }

  public AttributeAccessDescriptor getAttributeAccess() {
    return this.attributeAccess;
  }

  public MethodAccessDescriptor getMethodAccess() {
    return this.methodAccess;
  }
}
