// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class MethodAccessItemDto implements Serializable {
  private static final long serialVersionUID = -8725436837554348909L;

  private final int methodId;
  private final MethodAccessModeTypeDto accessMode;

  public MethodAccessItemDto(final int methodId, final MethodAccessModeTypeDto accessMode) {
    this.methodId = methodId;
    this.accessMode = accessMode;
  }

  public int getMethodId() {
    return this.methodId;
  }

  public MethodAccessModeTypeDto getAccessMode() {
    return this.accessMode;
  }
}
