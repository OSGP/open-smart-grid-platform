// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AttributeAccessDescriptor implements Serializable {
  private static final long serialVersionUID = 7637901931690262582L;

  private final List<AttributeAccessItem> attributeAccessItem;

  public AttributeAccessDescriptor(final List<AttributeAccessItem> attributeAccessItem) {
    this.attributeAccessItem = Collections.unmodifiableList(attributeAccessItem);
  }

  public List<AttributeAccessItem> getAttributeAccessItem() {
    return new ArrayList<>(this.attributeAccessItem);
  }
}
