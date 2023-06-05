// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
