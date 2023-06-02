//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class MethodAccessItem implements Serializable {
  private static final long serialVersionUID = -4754883636623267158L;

  private final int methodId;
  private final MethodAccessModeType accessMode;

  public MethodAccessItem(final int methodId, final MethodAccessModeType accessMode) {
    this.methodId = methodId;
    this.accessMode = accessMode;
  }

  public int getMethodId() {
    return this.methodId;
  }

  public MethodAccessModeType getAccessMode() {
    return this.accessMode;
  }
}
