//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
