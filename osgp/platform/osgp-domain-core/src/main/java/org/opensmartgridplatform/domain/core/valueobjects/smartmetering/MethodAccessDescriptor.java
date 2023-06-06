// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MethodAccessDescriptor implements Serializable {
  private static final long serialVersionUID = 8227300665604316411L;

  private final List<MethodAccessItem> methodAccessItem;

  public MethodAccessDescriptor(final List<MethodAccessItem> methodAccessItem) {
    this.methodAccessItem = Collections.unmodifiableList(methodAccessItem);
  }

  public List<MethodAccessItem> getMethodAccessItem() {
    return new ArrayList<>(this.methodAccessItem);
  }
}
