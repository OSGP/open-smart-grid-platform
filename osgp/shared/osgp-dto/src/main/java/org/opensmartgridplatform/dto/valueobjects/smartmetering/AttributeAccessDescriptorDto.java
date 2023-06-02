//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
