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
