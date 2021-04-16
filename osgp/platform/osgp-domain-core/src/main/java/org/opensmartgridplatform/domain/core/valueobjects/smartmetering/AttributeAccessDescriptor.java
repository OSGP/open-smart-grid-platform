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
