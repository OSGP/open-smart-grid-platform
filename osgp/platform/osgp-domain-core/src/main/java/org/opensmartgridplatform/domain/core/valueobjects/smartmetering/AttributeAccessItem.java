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
