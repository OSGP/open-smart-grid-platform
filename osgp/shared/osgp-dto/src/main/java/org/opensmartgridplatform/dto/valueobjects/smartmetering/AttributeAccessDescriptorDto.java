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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AttributeAccessDescriptorDto implements Serializable {
  private static final long serialVersionUID = 5897985162264224757L;

  private final List<AttributeAccessItemDto> attributeAccessItem;

  public AttributeAccessDescriptorDto(final List<AttributeAccessItemDto> attributeAccessItem) {
    this.attributeAccessItem = Collections.unmodifiableList(attributeAccessItem);
  }

  public List<AttributeAccessItemDto> getAttributeAccessItem() {
    return new ArrayList<>(this.attributeAccessItem);
  }
}
