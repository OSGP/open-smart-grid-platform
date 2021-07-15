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

public class MethodAccessDescriptorDto implements Serializable {
  private static final long serialVersionUID = -5415745885679494633L;

  private final List<MethodAccessItemDto> methodAccessItem;

  public MethodAccessDescriptorDto(final List<MethodAccessItemDto> methodAccessItem) {
    this.methodAccessItem = Collections.unmodifiableList(methodAccessItem);
  }

  public List<MethodAccessItemDto> getMethodAccessItem() {
    return new ArrayList<>(this.methodAccessItem);
  }
}
