//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class AttributeAccessItem implements Serializable {
  private static final long serialVersionUID = -4901070503296557000L;

  private final int attributeId;
  private final AttributeAccessModeType accessMode;
  private final AccessSelectorList accessSelectors;

  public AttributeAccessItem(
      final int attributeId,
      final AttributeAccessModeType accessMode,
      final AccessSelectorList accessSelectors) {
    this.attributeId = attributeId;
    this.accessMode = accessMode;
    this.accessSelectors = accessSelectors;
  }

  public int getAttributeId() {
    return this.attributeId;
  }

  public AttributeAccessModeType getAccessMode() {
    return this.accessMode;
  }

  public AccessSelectorList getAccessSelectors() {
    return this.accessSelectors;
  }
}
