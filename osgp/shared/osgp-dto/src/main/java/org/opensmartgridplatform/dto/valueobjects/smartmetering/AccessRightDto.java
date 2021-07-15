/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class AccessRightDto implements Serializable {
  private static final long serialVersionUID = 2406555055071093801L;

  private final AttributeAccessDescriptorDto attributeAccess;
  private final MethodAccessDescriptorDto methodAccess;

  public AccessRightDto(
      final AttributeAccessDescriptorDto attributeAccess,
      final MethodAccessDescriptorDto methodAccess) {
    this.attributeAccess = attributeAccess;
    this.methodAccess = methodAccess;
  }

  public AttributeAccessDescriptorDto getAttributeAccess() {
    return this.attributeAccess;
  }

  public MethodAccessDescriptorDto getMethodAccess() {
    return this.methodAccess;
  }
}
