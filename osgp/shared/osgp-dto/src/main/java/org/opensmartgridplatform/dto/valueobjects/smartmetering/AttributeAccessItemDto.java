// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class AttributeAccessItemDto implements Serializable {
  private static final long serialVersionUID = -6806206173336735187L;

  private final int attributeId;
  private final AttributeAccessModeTypeDto accessMode;
  private final AccessSelectorListDto accessSelectors;

  public AttributeAccessItemDto(
      final int attributeId,
      final AttributeAccessModeTypeDto accessMode,
      final AccessSelectorListDto accessSelectors) {
    this.attributeId = attributeId;
    this.accessMode = accessMode;
    this.accessSelectors = accessSelectors;
  }

  public int getAttributeId() {
    return this.attributeId;
  }

  public AttributeAccessModeTypeDto getAccessMode() {
    return this.accessMode;
  }

  public AccessSelectorListDto getAccessSelectors() {
    return this.accessSelectors;
  }
}
