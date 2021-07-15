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
