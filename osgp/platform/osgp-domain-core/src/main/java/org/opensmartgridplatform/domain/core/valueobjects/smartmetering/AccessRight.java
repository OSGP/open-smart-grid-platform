/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
